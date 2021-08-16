package at.co.netconsulting.wakeonlan;

import java.util.Date;

import lombok.Data;

@Data
public final class EntryPoj {

	private final String title;
	private final String author;
	private final Date postDate;
	private final int icon;
}