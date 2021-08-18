package at.co.netconsulting.wakeonlan;

import java.text.DateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import at.co.netconsulting.wakeonlan.poj.EntryPoj;

/**
 * Adapts EntryPoj objects onto views for lists
 */
public final class EntryAdapter extends ArrayAdapter<EntryPoj> {

	private final int newsItemLayoutResource;

	public EntryAdapter(final Context context, final int newsItemLayoutResource) {
		super(context, 0);
		this.newsItemLayoutResource = newsItemLayoutResource;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		
		// We need to get the best view (re-used if possible) and then
		// retrieve its corresponding ViewHolder, which optimizes lookup efficiency
		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final EntryPoj entry = getItem(position);
		
		// Setting the title view is straightforward
		viewHolder.hostname.setText(entry.getHostname());
		viewHolder.groupname.setText(entry.getGroup_name());
		viewHolder.ipAddress.setText(entry.getIp_address());
		viewHolder.nicmac.setText(entry.getNic_mac());
		viewHolder.comment.setText(entry.getComment());

		// Setting image view is also simple
		//viewHolder.imageView.setImageResource(entry.getIcon());
		
		return view;
	}

	private View getWorkingView(final View convertView) {
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;
		
		if(null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater)context.getSystemService
		      (Context.LAYOUT_INFLATER_SERVICE);
			
			workingView = inflater.inflate(newsItemLayoutResource, null);
		} else {
			workingView = convertView;
		}
		
		return workingView;
	}
	
	private ViewHolder getViewHolder(final View workingView) {
		// The viewHolder allows us to avoid re-looking up view references
		// Since views are recycled, these references will never change
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;
		
		
		if(null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();
			
			viewHolder.hostname = (TextView) workingView.findViewById(R.id.hostname);
			viewHolder.groupname = (TextView) workingView.findViewById(R.id.groupname);
			//viewHolder.imageView = (ImageView) workingView.findViewById(R.id.news_entry_icon);
			viewHolder.ipAddress = (TextView) workingView.findViewById(R.id.ipaddress);
			viewHolder.nicmac = (TextView) workingView.findViewById(R.id.nicmac);
			viewHolder.comment = (TextView) workingView.findViewById(R.id.comment);
			
			workingView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) tag;
		}
		
		return viewHolder;
	}
	
	/**
	 * ViewHolder allows us to avoid re-looking up view references
	 * Since views are recycled, these references will never change
	 */
	private static class ViewHolder {
		public TextView hostname;
		public TextView groupname;
		//public ImageView imageView;
		public TextView ipAddress;
		public TextView nicmac;
		public TextView comment;
	}
	
	
}