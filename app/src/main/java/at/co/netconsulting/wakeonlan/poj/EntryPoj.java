package at.co.netconsulting.wakeonlan.poj;

import android.widget.ImageView;
import lombok.Data;

@Data
public final class EntryPoj {
	//private final ImageView imageView;
	private final int id;
	private final String hostname;
	private final String group_name;
	private final String ip_address;
	private final String broadcast;
	private final String nic_mac;
	private final String comment;
}