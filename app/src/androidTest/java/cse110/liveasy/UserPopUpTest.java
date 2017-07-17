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
public class UserPopUpTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void userPopUpTest() throws InterruptedException {

        Thread.sleep(4000);

        ViewInteraction appCompatEditText = onView(
                withId(R.id.input_username));
        appCompatEditText.perform(scrollTo(), replaceText("UserPopUpTest"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.input_password));
        appCompatEditText2.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule2 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(3000);

        ViewInteraction imageView = onView(
                allOf(withId(R.id.main_profile_image1),
                        childAtPosition(
                                allOf(withId(R.id.activity_main),
                                        childAtPosition(
                                                withId(R.id.fragment_home1),
                                                0)),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction circleImageView = onView(
                allOf(withId(R.id.main_profile_image1),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.fragment_home1)))),
                        isDisplayed()));
        circleImageView.perform(click());

        // wait for pop up to load
        Thread.sleep(2000);

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.profile_image_popup),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button2), withText("PROFILE"),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ActivityTestRule<ProfileActivity> mActivityTestRule3 = new ActivityTestRule<>(ProfileActivity.class);

        Thread.sleep(3000);

        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.profile_image),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule4 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(3000);

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

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
