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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateGroupPage {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void createGroupPage() throws InterruptedException {

        Thread.sleep(4000);

        //Working up to context
        ViewInteraction appCompatEditText = onView(
                withId(R.id.input_username));
        appCompatEditText.perform(scrollTo(), replaceText("CreateGroupPageTest"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.input_password));
        appCompatEditText2.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ActivityTestRule<NavDrawerActivity> mActivityTestRule2 = new ActivityTestRule<>(NavDrawerActivity.class);

        Thread.sleep(4000);


        //GIVEN I'm on the homepage
        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.activity_main),
                        childAtPosition(
                                allOf(withId(R.id.fragment_home1),
                                        childAtPosition(
                                                withId(R.id.content_frame),
                                                0)),
                                0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        //WHEN I click the "create group" button
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button_creategroup), withText("Create Group"),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.fragment_home1)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ActivityTestRule<CreateGroup> mActivityTestRule3 = new ActivityTestRule<>(CreateGroup.class);

        //THEN I will be taken to the create group page
        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.activity_create_group),
                        isDisplayed()));
        relativeLayout2.check(matches(isDisplayed()));

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
