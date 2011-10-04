package org.dyndns.warenix.imageGallery;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3488595966211138853L;
	ArrayList<String> imageList;

	public ImageList() {
		imageList = new ArrayList<String>();
	}

	public void addImage(String url) {
		imageList.add(url);
	}

	public ArrayList<String> getImageList() {
		return imageList;
	}

}
