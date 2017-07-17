package cse110.liveasy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import de.hdodenhof.circleimageview.CircleImageView;

import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * Fragment that presents the logic for when there is more than 4 users in a group, in which case,
 * all the users will be listed.
 */
public class Home5 extends Fragment {

    ListView list;

    public Home5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home5more, container, false);
        final String[] user = new String[((NavDrawerActivity)getActivity()).group.num_users];
        String[] allMembers = ((NavDrawerActivity)getActivity()).getMembers();
        user[0] = ((NavDrawerActivity)getActivity()).username;

        // loop through list of member and create a list without main user
        int j = 1;
        for (int i = 0; i < ((NavDrawerActivity)getActivity()).group.num_users; i++) {
            if (!allMembers[i].equals(((NavDrawerActivity)getActivity()).username)) {
                user[j] = allMembers[i];
                j++;
            }
        }

        // set up for the custom list adapter
        final String[] URLs = new String[((NavDrawerActivity)getActivity()).group.num_users];
        String[] emails = new String[((NavDrawerActivity)getActivity()).group.num_users];
        String[] numbers = new String[((NavDrawerActivity)getActivity()).group.num_users];

        // set up the above arrays to get the appropriate information
        for (int i = 0; i < ((NavDrawerActivity)getActivity()).group.num_users; i++) {
            Map<String, Object> member = (Map<String, Object>)((NavDrawerActivity)getActivity()).group.members.get(user[i]);
            URLs[i] = (String) member.get("photo_url");
            numbers[i] = (String) member.get("phone_number");
            emails[i] = (String) member.get("email");
        }

        // use our customlist to make a listview
        CustomList adapter = new CustomList(getActivity(), user, URLs, numbers, emails);
        list=(ListView) view.findViewById(R.id.list);
        list.setAdapter(adapter);

        // set each list item to be clickable to a popup
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {

                // make a new profile according to the position of list item clicked
                Map<String, Object> currentMember = (Map <String, Object>)((NavDrawerActivity)getActivity()).group.members.get(user[position]);
                final Profile newProfile = new Profile(currentMember, user[position]);

                ((NavDrawerActivity) getActivity()).toProfilePopup(v, newProfile, user[position]);


            }
        });

        // set up the group image and make it clickable
        CircleImageView groupPic = (CircleImageView) view.findViewById(R.id.group_image5);
        Picasso.with((NavDrawerActivity)getContext())
                .load(((NavDrawerActivity)getActivity()).group.photo_url)
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.blank)
                .into(groupPic);

        // Inflate the layout for this fragment
        return view;
    }

}