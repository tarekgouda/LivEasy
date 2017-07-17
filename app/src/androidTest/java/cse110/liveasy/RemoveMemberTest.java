package cse110.liveasy;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RemoveMemberTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void removeMemberTest() throws InterruptedException {

        Thread.sleep(4000);

        ViewInteraction appCompatEditText = onView(
                withId(R.id.input_username));
        appCompatEditText.perform(scrollTo(), replaceText("testuser2"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.input_password));
        appCompatEditText2.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule2 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(2500);

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Remove Members from Group"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ActivityTestRule<RemoveUserFromGroup> mActivityTestRule3 = new ActivityTestRule<>(RemoveUserFromGroup.class);

        Thread.sleep(4000);

        ViewInteraction textView = onView(
                allOf(withText("Remove Members from Group"),
                        isDisplayed()));
        textView.check(matches(withText("Remove Members from Group")));

        ViewInteraction textView2 = onView(
                allOf(withText("testuser"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("testuser")));

        ViewInteraction button = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                1),
                        1),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withText("Remove"), isDisplayed()));
        button2.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        Thread.sleep(4000);

        ActivityTestRule<NavDrawerActivity> mActivityTestRule4 = new ActivityTestRule<>(NavDrawerActivity.class);

        ViewInteraction imageView = onView(
                allOf(withId(R.id.main_profile_image1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.fragment_home1),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        Thread.sleep(3000);

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ActivityTestRule<LoginActivity> mActivityTestRule5 = new ActivityTestRule<>(LoginActivity.class);

        ViewInteraction appCompatEditText6 = onView(
                withId(R.id.input_username));
        appCompatEditText6.perform(scrollTo(), replaceText("testuser"), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                withId(R.id.input_password));
        appCompatEditText7.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.JoinGroup), withText("Join Group"),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.fragment_home1)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ActivityTestRule<JoinGroup> mActivityTestRule6 = new ActivityTestRule<>(JoinGroup.class);

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.editText6),
                        withParent(allOf(withId(R.id.activity_create_group),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("-KXddDOKBlrG-FVHudxG"), closeSoftKeyboard());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.button9), withText("Join"), isDisplayed()));
        appCompatButton6.perform(click());

        Thread.sleep(4000);

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule7 = new ActivityTestRule<>(NavDrawerActivity.class);

        ViewInteraction appCompatImageButton5 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton5.perform(click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView4.perform(click());

        ActivityTestRule<LoginActivity> mActivityTestRule8 = new ActivityTestRule<>(LoginActivity.class);

        ViewInteraction appCompatEditText10 = onView(
                withId(R.id.input_username));
        appCompatEditText10.perform(scrollTo(), replaceText("testuser2"), closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                withId(R.id.input_password));
        appCompatEditText11.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton8.perform(scrollTo(), click());

        Thread.sleep(4000);

        ViewInteraction appCompatImageButton66 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton66.perform(click());

        Thread.sleep(4000);

        ViewInteraction appCompatCheckedTextView5 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Manage Requests (1)"), isDisplayed()));
        appCompatCheckedTextView5.perform(click());

        ActivityTestRule<ManageRequests> mActivityTestRule10 = new ActivityTestRule<>(ManageRequests.class);

        Thread.sleep(4000);

        ViewInteraction textView3 = onView(
                allOf(withText("testuser"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("testuser")));

        ViewInteraction button3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                1),
                        1),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction button4 = onView(
                allOf(withText("Accept"), isDisplayed()));
        button4.perform(click());

        ViewInteraction appCompatImageButton7 = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton7.perform(click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule11 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(4000);

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.main_profile_image),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.member_image_2_1),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton8 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton8.perform(click());

        ViewInteraction appCompatCheckedTextView6 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView6.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
