package ethien.loop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Christian on 2/4/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private HashMap<String, ArrayList<String>> listChildData;
    private ArrayList<String> listDataHeader;

    public ExpandableListAdapter(Context context, HashMap<String, ArrayList<String>> hashMap,
                                 ArrayList<String> list)
    {
        this.context = context;
        this.listChildData = hashMap;
        this.listDataHeader = list;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView,
                             ViewGroup parent)
    {
        String groupTitle = (String) getGroup(groupPosition);
        if (convertView == null)
        {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, parent, false);
        }
        TextView parentTextView = (TextView) convertView.findViewById(R.id.group_text_parent);
        parentTextView.setText(groupTitle);
        return convertView;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        String childText = (String) getChild(groupPosition, childPosition);
        String childURL = "";
        Integer childType = getChildType(groupPosition, childPosition);

        boolean cat = false;
        if(!childText.contains("https://"))
        {
            cat = true;
        }
        else
        {
            int location = childText.indexOf("=");
            childURL = childText.substring(location + 1);
            childText = childText.substring(0, location);
        }
        if (convertView == null || convertView.getTag() != childType)
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(cat)
            {
                convertView = inflater.inflate(R.layout.child_item_header, parent, false);
            }
            else
            {
                convertView = inflater.inflate(R.layout.child_item, parent, false);
            }
        }
        TextView childTextView = (TextView) convertView.findViewById(R.id.child_text);
        if(childTextView != null)
            if(!cat)
            {
                convertView.setTag(childURL);
                Log.d("URL", childURL);
            }
            String formattedChildText = capitalize(childText.replace("/r/", ""));
            childTextView.setText(formattedChildText);
        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        String child = getChild(groupPosition, childPosition).toString();
        if(!child.contains("https://"))
            return false;
        return true;
    }
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.listChildData.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.listChildData.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public int getGroupCount()
    {
        return this.listDataHeader.size();
    }
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
