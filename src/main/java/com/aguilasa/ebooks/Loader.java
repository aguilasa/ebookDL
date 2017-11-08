package com.aguilasa.ebooks;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Loader {
	private static final String PAGES = "Pages";
	private static final String YEAR = "Year";
	private static final String PUBLISHER = "Publisher";
	private static final String LANGUAGE = "Language";
	private static final String FILESIZE = "File Size";
	private static final String FILEFORMAT = "File Format";
	private static final String BASE_URL = "http://ebook-dl.com";
	private static final String SEARCH = "/Search/";

	private Document document;
	private int page = 1;
	private int pages;
	private boolean reload = true;
	private Set<EbookPost> ebookPosts = new LinkedHashSet<>();
	private String subject = "";

	public Loader(String subject) throws IOException {
		this.subject = subject;
		loadDocument();
		loadNumPages();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Set<EbookPost> getEbookPosts() {
		return ebookPosts;
	}

	private void loadNumPages() {
		pages = 1;
		Element last = document.select("ul.pagination li a").last();
		if (last != null) {
			pages = Integer.valueOf(last.text());
		}
	}

	public void incPage() {
		if (page < pages) {
			page++;
			reload = true;
		}
	}

	public void decPage() {
		if (page > 1) {
			page--;
			reload = true;
		}
	}

	private String getUrl() {
		StringBuilder url = new StringBuilder();
		url.append(BASE_URL).append(SEARCH).append(subject);
		if (page > 1) {
			url.append("/page/").append(page);
		}
		return url.toString();
	}

	private void loadDocument() throws IOException {
		if (reload) {
			document = Jsoup.connect(getUrl()).get();
			reload = false;
		}
	}

	public void loadEbooksPage() throws IOException {
		loadDocument();
		Elements articles = document.select("article");
		if (!articles.isEmpty()) {
			for (Element article : articles) {
				try {
					EbookPost EbookPost = loadEbookPost(article);
					ebookPosts.add(EbookPost);
				} catch (RuntimeException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	public void loadAllEbooks() throws IOException {
		do {
			System.out.println(String.format("Página: %d de %d", page, pages));
			loadEbooksPage();
			incPage();
		} while (page < pages);
	}

	private EbookPost loadEbookPost(Element element) {
		EbookPost ebookPost = new EbookPost();

		Element titleElement = element.select("h2.entry-title a").first();
		ebookPost.setTitle(titleElement.text());
		ebookPost.setHref(titleElement.attr("href").trim());

		Elements categoryElements = element.select("ul.list-inline li a");
		for (Element e : categoryElements) {
			ebookPost.addCategory(e.text().trim(), e.attr("href").trim());
		}

		Element resumeElement = element.select("div.entry-content p").first();
		ebookPost.setResume(resumeElement.text());

		Element imgElement = element.select("div.thumb-wrapper a img").first();
		ebookPost.setImage(imgElement.attr("src").trim());

		loadDetails(ebookPost);

		return ebookPost;
	}

	private void loadDetails(EbookPost ebookPost) {
		try {
			Document d = Jsoup.connect(BASE_URL.concat(ebookPost.getHref())).get();
			Elements elements = d.select("ul.portfolio-meta li");
			for (Element e : elements) {
				String text = e.text().trim();
				if (text.startsWith(PAGES)) {
					text = processText(text, PAGES);
					ebookPost.setPages(Integer.valueOf(text));
				} else if (text.startsWith(YEAR)) {
					text = processText(text, YEAR);
					ebookPost.setYear(Integer.valueOf(text));
				} else if (text.startsWith(PUBLISHER)) {
					text = processText(text, PUBLISHER);
					ebookPost.setPublisher(text);
				} else if (text.startsWith(LANGUAGE)) {
					text = processText(text, LANGUAGE);
					ebookPost.setLanguage(text);
				} else if (text.startsWith(FILESIZE)) {
					text = processText(text, FILESIZE);
					ebookPost.setFileSize(text);
				} else if (text.startsWith(FILEFORMAT)) {
					text = processText(text, FILEFORMAT);
					ebookPost.setFileFormat(text);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String processText(String text, String constant) {
		return text.replace(constant, "").trim();
	}
}
