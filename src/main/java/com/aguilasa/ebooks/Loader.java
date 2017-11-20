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
		Element last = document.select("nav.pagination li a").last();
		if (last != null) {
			pages = Integer.valueOf(last.text());
		}
	}

	public void incPage() {
		if (page <= pages) {
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
			url.append("/pg/").append(page);
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
		Elements articles = document.select("div.four.shop.columns");
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
		while (page <= pages) {
			System.out.println(String.format("Página: %d de %d", page, pages));
			loadEbooksPage();
			incPage();
		}
	}

	private EbookPost loadEbookPost(Element element) {
		EbookPost ebookPost = new EbookPost();

		Element titleElement = element.select("section h5").first();
		ebookPost.setTitle(titleElement.text());

		titleElement = element.select("a.product-button").first();

		ebookPost.setHref(titleElement.attr("href").trim());

		// Elements categoryElements = element.select("ul.list-inline li a");
		// for (Element e : categoryElements) {
		// ebookPost.addCategory(e.text().trim(), e.attr("href").trim());
		// }
		//
		// Element resumeElement = element.select("div.entry-content
		// p").first();
		// ebookPost.setResume(resumeElement.text());

		Element imgElement = element.select("div.mediaholder a img").first();
		ebookPost.setImage(imgElement.attr("src").trim());

		loadDetails(ebookPost);

		return ebookPost;
	}

	private void loadDetails(EbookPost ebookPost) {
		try {
			Document d = Jsoup.connect(BASE_URL.concat(ebookPost.getHref())).get();
			Elements elements = d.select("table.basic-table tr");
			for (Element e : elements) {
				Elements th = e.select("th");
				Elements td = e.select("td");
				String thText = th.text().trim();
				String tdText = td.text().trim();
				if (thText.startsWith(PAGES)) {
					tdText = processText(tdText, PAGES);
					ebookPost.setPages(Integer.valueOf(tdText));
				} else if (thText.startsWith(YEAR)) {
					tdText = processText(tdText, YEAR);
					ebookPost.setYear(Integer.valueOf(tdText));
				} else if (thText.startsWith(PUBLISHER)) {
					tdText = processText(tdText, PUBLISHER);
					ebookPost.setPublisher(tdText);
				} else if (thText.startsWith(LANGUAGE)) {
					tdText = processText(tdText, LANGUAGE);
					ebookPost.setLanguage(tdText);
				} else if (thText.startsWith(FILESIZE)) {
					tdText = processText(tdText, FILESIZE);
					ebookPost.setFileSize(tdText);
				} else if (thText.startsWith(FILEFORMAT)) {
					tdText = processText(tdText, FILEFORMAT);
					ebookPost.setFileFormat(tdText);
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
