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

/* TEST SCENARIO:

  Given I have the nav drawer open
  When I click on the pending request option
  Then I will be taken to a page containing all pending requests */


public class ManageRequestsPageTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void manageRequestsPageTest() throws InterruptedException{
        Thread.sleep(4000);

        ViewInteraction appCompatEditText = onView(
                withId(R.id.input_username));
        appCompatEditText.perform(scrollTo(), replaceText("piano"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.input_password));
        appCompatEditText2.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule = new ActivityTestRule<>(NavDrawerActivity.class);

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction checkedTextView = onView(
                allOf(withId(R.id.design_menu_item_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_navigation_view),
                                        2),
                                0),
                        isDisplayed()));
        checkedTextView.check(matches(isDisplayed()));

        ActivityTestRule<ManageRequests> mActivityTestRule2 = new ActivityTestRule<>(ManageRequests.class);

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Manage Requests"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        Thread.sleep(4000);

        ViewInteraction textView4 = onView(
                allOf(withText("Pending Requests"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("Pending Requests")));
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
