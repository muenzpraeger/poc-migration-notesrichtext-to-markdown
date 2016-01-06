/**
 * 
 */

package com.winkelmeyer.richtext2markdown.model;

import com.google.gson.annotations.SerializedName;

/**
 * This class reflects the model of a simple view entry.
 */
public class ViewEntry {

	private String	body;
	private String	number;
	private String	title;
	@SerializedName("@unid")
	private String	unid;
	private Object	entrydata;

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the unid
	 */
	public String getUnid() {
		return unid;
	}

	/**
	 * @param unid
	 *            the unid to set
	 */
	public void setUnid(String unid) {
		this.unid = unid;
	}

	/**
	 * @return the entrydata
	 */
	public Object getEntrydata() {
		return entrydata;
	}

	/**
	 * @param entrydata
	 *            the entrydata to set
	 */
	public void setEntrydata(Object entrydata) {
		this.entrydata = entrydata;
	}

}
