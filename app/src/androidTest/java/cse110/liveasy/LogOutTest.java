package cse110.liveasy;


import android.support.test.espresso.ViewInteraction;
import android.support.design.widget.TextInputLayout;
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
   When I click on the logout option
   Then I will be logged out and sent to the login screen
 */

public class LogOutTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void logOutTest2() throws InterruptedException {

        Thread.sleep(4000);

        ViewInteraction appCompatEditText = onView(
                withId(R.id.input_username));
        appCompatEditText.perform(scrollTo(), click());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.input_username));
        appCompatEditText2.perform(scrollTo(), replaceText("piano"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.input_password));
        appCompatEditText3.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

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
                                        8),
                                0),
                        isDisplayed()));
        checkedTextView.check(matches(isDisplayed()));

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Log Out"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ActivityTestRule<LoginActivity> mActivityTestRule2 = this.mActivityTestRule;

        ViewInteraction appCompatEditText4 = onView(
                withId(R.id.input_username));
        appCompatEditText4.perform(scrollTo(), click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.input_username),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

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
