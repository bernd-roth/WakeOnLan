package at.co.netconsulting.wakeonlan.poj;

import android.widget.ImageView;

import java.util.Date;

import lombok.Data;

@Data
public final class EntryPoj {
	//private final ImageView imageView;
	private final String hostname;
	private final String group_name;
	private final String ip_address;
	private final String nic_mac;
	private final String comment;
}