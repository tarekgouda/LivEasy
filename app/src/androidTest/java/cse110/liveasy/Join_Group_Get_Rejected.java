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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
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
public class Join_Group_Get_Rejected {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void join_Group_Get_Rejected() throws InterruptedException {

        Thread.sleep(4000);

        ViewInteraction appCompatEditText = onView(
                withId(R.id.input_username));
        appCompatEditText.perform(scrollTo(), click());

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.input_username));
        appCompatEditText3.perform(scrollTo(), replaceText("kelvinator"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                withId(R.id.input_password));
        appCompatEditText4.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule2 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(3000);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.JoinGroup), withText("Join Group"),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ActivityTestRule<CreateGroup> mActivityTestRule3 = new ActivityTestRule<>(CreateGroup.class);

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.editText6),
                        withParent(allOf(withId(R.id.activity_create_group),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("-KXo4tymevUa_WWu5Wr1"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.button9), withText("Join"), isDisplayed()));
        appCompatButton3.perform(click());

        Thread.sleep(4000);

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule4 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(4000);

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

        ViewInteraction appCompatEditText6 = onView(
                withId(R.id.input_username));
        appCompatEditText6.perform(scrollTo(), click());

        ViewInteraction appCompatEditText7 = onView(
                withId(R.id.input_username));
        appCompatEditText7.perform(scrollTo(), replaceText("goose1"), closeSoftKeyboard());

        ViewInteraction appCompatEditText8 = onView(
                withId(R.id.input_password));
        appCompatEditText8.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton5.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule5 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(4000);

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Manage Requests (1)"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ActivityTestRule<ManageRequests> mActivityTestRule6 = new ActivityTestRule<>(ManageRequests.class);

        Thread.sleep(4000);

        ViewInteraction button = onView(
                allOf(withText("Reject"), isDisplayed()));
        button.perform(click());

        ViewInteraction appCompatImageButton24 = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton24.perform(click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule7 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(4000);

        ViewInteraction appCompatImageButton21 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        isDisplayed()));
        appCompatImageButton21.perform(click());

        ViewInteraction appCompatCheckedTextView3 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView3.perform(click());

        ActivityTestRule<LoginActivity> mActivityTestRule8 = new ActivityTestRule<>(LoginActivity.class);

        ViewInteraction appCompatEditText9 = onView(
                withId(R.id.input_username));
        appCompatEditText9.perform(scrollTo(), click());

        ViewInteraction appCompatEditText10 = onView(
                withId(R.id.input_username));
        appCompatEditText10.perform(scrollTo(), replaceText("kelvinator"), closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                withId(R.id.input_password));
        appCompatEditText11.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton6.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule9 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(4000);

        ViewInteraction button2 = onView(
                allOf(withId(R.id.button_creategroup),
                        childAtPosition(
                                allOf(withId(R.id.activity_main),
                                        childAtPosition(
                                                withId(R.id.fragment_home1),
                                                0)),
                                1),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView4.perform(click());

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
