package com.android.xiaow.jx3bbs.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bernda implements Comparable<Bernda> {
	// Ìû×ÓÀà±ð
	public boolean type;

	public String url;
	public String name;
	public String author;
	public int scane;
	public int refuse;
	public String item;
	public String item_url;
	public String last_time;
	public int max_page = 0;
	public String parent;
	public String lastName;

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Bernda) {
			Bernda o2 = (Bernda) o;
			if (o2.url == null)
				return super.equals(o);
			return url.equals(((Bernda) o).url);
		}
		return super.equals(o);
	}

	@Override
	public int compareTo(Bernda another) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm");
		try {
			Date date = sdf.parse(last_time);
			Date date2 = sdf.parse(another.last_time);
			if (date.after(date2)) {
				return 1;
			} else {
				return -1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
