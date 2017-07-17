package cse110.liveasy;


import android.support.design.widget.TextInputLayout;
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

import java.util.IllegalFormatCodePointException;

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

/***
 Given I am on the log in page
 AND input the correct information
 AND have an account
 When I click “Login”
 Then I will be taken to the home page containing me and two buttons: Create Group and Join Group

 Credential
 username: logintest
 password: password
 email: logintest@example.com
 ***/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class Login_to_NavDrawerActivity {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void login_to_NavDrawerActivity() throws InterruptedException {

        Thread.sleep(4000);

        ViewInteraction appCompatEditText = onView(
                withId(R.id.input_username));
        appCompatEditText.perform(scrollTo(), replaceText("logintest"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.input_password));
        appCompatEditText2.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.input_username), withText("logintest"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("logintest")));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule = new ActivityTestRule<>(NavDrawerActivity.class);

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

        ViewInteraction button = onView(
                allOf(withId(R.id.button_creategroup),
                        childAtPosition(
                                allOf(withId(R.id.activity_main),
                                        childAtPosition(
                                                withId(R.id.fragment_home1),
                                                0)),
                                1),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.JoinGroup),
                        childAtPosition(
                                allOf(withId(R.id.activity_main),
                                        childAtPosition(
                                                withId(R.id.fragment_home1),
                                                0)),
                                2),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton6 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton6.perform(click());

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
