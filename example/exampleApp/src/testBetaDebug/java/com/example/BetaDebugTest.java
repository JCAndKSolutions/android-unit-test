package com.example;

import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class BetaDebugTest {
  @Test
  public void testBetaReleaseTypeAndDebugBuildType() {
    MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
    TextView releaseTypeView = (TextView) activity.findViewById(R.id.ReleaseTextView);
    assertThat(releaseTypeView).hasText("Beta");
    TextView buildTypeView = (TextView) activity.findViewById(R.id.BuildTextView);
    assertThat(buildTypeView).hasText("Debug");
  }
}
