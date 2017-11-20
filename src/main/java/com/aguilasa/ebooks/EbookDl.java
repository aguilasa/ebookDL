package com.aguilasa.ebooks;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class EbookDl {

	public static void main(String[] args) {
		boolean filter = true;
		try {
			Loader loader = new Loader("xamarin");
			loader.loadAllEbooks();
			Set<EbookPost> ebookPosts = loader.getEbookPosts();
			if (filter) {
				ebookPosts = ebookPosts.stream().filter(l -> l.getYear() >= 2016).collect(Collectors.toSet());
			}
			for (EbookPost e : ebookPosts) {
				System.out.println(e);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
