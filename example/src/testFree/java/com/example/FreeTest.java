package com.example;

import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class FreeTest {
  @Test
  public void testFreePricingType() {
    MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
    TextView pricingTypeView = (TextView) activity.findViewById(R.id.PricingTextView);
    assertThat(pricingTypeView).hasText("Free");
  }
}
