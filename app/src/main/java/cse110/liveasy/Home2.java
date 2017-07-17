package cse110.liveasy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment that presents when there is exactly two users in a group, in which case, the profiles
 * will be aligned in a specific way.
 */

public class Home2 extends Fragment {

    public Home2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View theView = inflater.inflate(R.layout.fragment_home2, container, false);
        String[] membersList = ((NavDrawerActivity)getActivity()).getMembers();
        final String[] roommateList = new String[(membersList.length) - 1];
        final String mainUser = ((NavDrawerActivity)getActivity()).username;
        Map<String, Object> group = ((NavDrawerActivity)getActivity()).group.members;

        // loop through list of member and create a list without main user
        int j = 0;
        for (int i = 0; i < membersList.length; i++) {
            if (!membersList[i].equals(mainUser)) {
                roommateList[j] = membersList[i];
                j++;
            }
        }

        final Profile mainProfile = new Profile((Map<String, Object>) group.get(mainUser), mainUser);

        // set up main user's picutre and make it clickable to a popup
        CircleImageView selfie = (CircleImageView) theView.findViewById(R.id.main_profile_image);
        // get picture
        Picasso.with((NavDrawerActivity)getContext())
                .load(((NavDrawerActivity)getActivity()).user.photo_url)
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.blank)
                .into(selfie);

        selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((NavDrawerActivity) getActivity()).toProfilePopup(v, mainProfile, mainUser);
            }
        });

        // loop through each member and set up their pictures according to each slot
        for (int index = 0; index < roommateList.length; index++) {

            Map<String, Object> currentMember = (Map <String, Object>)((NavDrawerActivity)getActivity()).group.members.get(roommateList[index]);
            final Profile newProfile = new Profile(currentMember, roommateList[index]);
            final String membersName = roommateList[index];

            // switch case for each different slot
            switch(index) {
                case 0:
                    CircleImageView memberSelfie = (CircleImageView) theView.findViewById(R.id.member_image_2_1);

                    Picasso.with((NavDrawerActivity)getContext())
                            .load(newProfile.photo_url)
                            .resize(200,200)
                            .centerCrop()
                            .placeholder(R.drawable.blank)
                            .into(memberSelfie);

                    memberSelfie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ((NavDrawerActivity) getActivity()).toProfilePopup(v, newProfile, membersName);
                        }
                    });

            }

        }


        // set up the group image and make it clickable
        CircleImageView groupPic = (CircleImageView) theView.findViewById(R.id.group_image2);
        Picasso.with((NavDrawerActivity)getContext())
                .load(((NavDrawerActivity)getActivity()).group.photo_url)
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.blank)
                .into(groupPic);


        return theView;
    }

}
