package com.aguilasa.ebooks;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class EbookDl {

	public static void main(String[] args) {
		try {
			Loader loader = new Loader("artificial%20intelligence");
			loader.loadAllEbooks();
			Set<EbookPost> ebookPosts = loader.getEbookPosts();
//			ebookPosts = ebookPosts.stream().filter(l -> l.getYear() >= 2015).collect(Collectors.toSet());
			for (EbookPost e : ebookPosts) {
				System.out.println(e);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
