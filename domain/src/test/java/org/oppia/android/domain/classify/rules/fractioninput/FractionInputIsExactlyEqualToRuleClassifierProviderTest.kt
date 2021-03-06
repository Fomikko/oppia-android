package org.oppia.android.domain.classify.rules.fractioninput

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.BindsInstance
import dagger.Component
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oppia.android.app.model.Fraction
import org.oppia.android.app.model.InteractionObject
import org.oppia.android.domain.classify.RuleClassifier
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import kotlin.test.fail

/** Tests for [FractionInputIsExactlyEqualToRuleClassifierProvider]. */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(manifest = Config.NONE)
class FractionInputIsExactlyEqualToRuleClassifierProviderTest {
  private val NON_NEGATIVE_VALUE_0 = createNonNegativeInt(value = 0)
  private val WHOLE_NUMBER_123 = createWholeNumber(isNegative = false, value = 123)
  private val WHOLE_NUMBER_321 = createWholeNumber(isNegative = false, value = 321)
  private val FRACTION_2_OVER_4 = createFraction(isNegative = false, numerator = 2, denominator = 4)
  private val FRACTION_1_OVER_2 = createFraction(isNegative = false, numerator = 1, denominator = 2)
  private val FRACTION_123_OVER_1 =
    createFraction(isNegative = false, numerator = 123, denominator = 1)
  private val MIXED_NUMBER_123_1_OVER_2 =
    createMixedNumber(isNegative = false, wholeNumber = 123, numerator = 1, denominator = 2)
  private val MIXED_NUMBER_123_1_OVER_3 =
    createMixedNumber(isNegative = false, wholeNumber = 123, numerator = 1, denominator = 3)
  private val MIXED_NUMBER_NEGATIVE_123_1_OVER_2 =
    createMixedNumber(isNegative = true, wholeNumber = 123, numerator = 1, denominator = 2)

  @Inject
  internal lateinit var fractionInputIsExactlyEqualToRuleClassifierProvider:
    FractionInputIsExactlyEqualToRuleClassifierProvider

  private val isExactlyEqualClassifierProvider: RuleClassifier by lazy {
    fractionInputIsExactlyEqualToRuleClassifierProvider.createRuleClassifier()
  }

  @Test
  fun testEquals_wholeNumber123Answer_withWholeNumber123Input_bothValuesMatch() {
    val inputs = mapOf("f" to WHOLE_NUMBER_123)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = WHOLE_NUMBER_123,
        inputs = inputs
      )

    assertThat(matches).isTrue()
  }

  @Test
  fun testEquals_wholeNumber321Answer_withWholeNumber123Input_bothValuesDoNotMatch() {
    val inputs = mapOf("f" to WHOLE_NUMBER_123)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = WHOLE_NUMBER_321,
        inputs = inputs
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testEquals_fraction2Over4Answer_withFraction2Over4Input_bothValuesMatch() {
    val inputs = mapOf("f" to FRACTION_2_OVER_4)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = FRACTION_2_OVER_4,
        inputs = inputs
      )

    assertThat(matches).isTrue()
  }

  @Test
  fun testEquals_bothAnswerAndInputFractions_oneFractionReduced_bothValuesDoNotMatch() {
    val inputs = mapOf("f" to FRACTION_2_OVER_4)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = FRACTION_1_OVER_2,
        inputs = inputs
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testEquals_mixedNumber123And1Over2Answer_withMixedNumber123And1Over2Input_bothValuesMatch() {
    val inputs = mapOf("f" to MIXED_NUMBER_123_1_OVER_2)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = MIXED_NUMBER_123_1_OVER_2,
        inputs = inputs
      )

    assertThat(matches).isTrue()
  }

  @Test
  fun testEquals_mixedNum123And1Over3Answer_withMixedNum123And1Over2Input_bothValuesDoNotMatch() {
    val inputs = mapOf("f" to MIXED_NUMBER_123_1_OVER_2)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = MIXED_NUMBER_123_1_OVER_3,
        inputs = inputs
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testEquals_negativeMixedNumberAnswer_withPositiveMixedNumberInput_bothValuesDoNotMatch() {
    val inputs = mapOf("f" to MIXED_NUMBER_123_1_OVER_2)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = MIXED_NUMBER_NEGATIVE_123_1_OVER_2,
        inputs = inputs
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testEquals_wholeNumberAnswer_withMixedNumberInput_bothValuesDoNotMatch() {
    val inputs = mapOf("f" to MIXED_NUMBER_123_1_OVER_2)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = WHOLE_NUMBER_123,
        inputs = inputs
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testEquals_wholeNumber123Answer_withFraction123Over1Input_bothValuesDoNotMatch() {
    val inputs = mapOf("f" to FRACTION_123_OVER_1)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = WHOLE_NUMBER_123,
        inputs = inputs
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testEquals_mixedNumberAnswer_withFractionInput_bothValuesDoNotMatch() {
    val inputs = mapOf("f" to FRACTION_1_OVER_2)

    val matches =
      isExactlyEqualClassifierProvider.matches(
        answer = MIXED_NUMBER_123_1_OVER_2,
        inputs = inputs
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testEquals_nonNegativeInput_inputWithIncorrectType_throwsException() {
    val inputs = mapOf("f" to NON_NEGATIVE_VALUE_0)

    val exception = assertThrows(IllegalStateException::class) {
      isExactlyEqualClassifierProvider.matches(
        answer = FRACTION_2_OVER_4,
        inputs = inputs
      )
    }

    assertThat(exception)
      .hasMessageThat()
      .contains(
        "Expected input value to be of type FRACTION not NON_NEGATIVE_INT"
      )
  }

  @Test
  fun testEquals_missingInputF_throwsException() {
    val inputs = mapOf("y" to FRACTION_2_OVER_4)

    val exception = assertThrows(IllegalStateException::class) {
      isExactlyEqualClassifierProvider.matches(
        answer = FRACTION_2_OVER_4,
        inputs = inputs
      )
    }

    assertThat(exception)
      .hasMessageThat()
      .contains("Expected classifier inputs to contain parameter with name 'f' but had: [y]")
  }

  private fun createFraction(
    isNegative: Boolean,
    numerator: Int,
    denominator: Int
  ): InteractionObject {
    // Fraction-only numbers imply no whole number.
    return InteractionObject.newBuilder().setFraction(
      Fraction.newBuilder()
        .setIsNegative(isNegative)
        .setNumerator(numerator)
        .setDenominator(denominator)
        .build()
    ).build()
  }

  private fun createWholeNumber(isNegative: Boolean, value: Int): InteractionObject {
    // Whole number fractions imply '0/1' fractional parts.
    return InteractionObject.newBuilder().setFraction(
      Fraction.newBuilder()
        .setIsNegative(isNegative)
        .setWholeNumber(value)
        .setNumerator(0)
        .setDenominator(1)
        .build()
    ).build()
  }

  private fun createMixedNumber(
    isNegative: Boolean,
    wholeNumber: Int,
    numerator: Int,
    denominator: Int
  ): InteractionObject {
    return InteractionObject.newBuilder().setFraction(
      Fraction.newBuilder()
        .setIsNegative(isNegative)
        .setWholeNumber(wholeNumber)
        .setNumerator(numerator)
        .setDenominator(denominator)
        .build()
    ).build()
  }

  private fun createNonNegativeInt(value: Int): InteractionObject {
    return InteractionObject.newBuilder().setNonNegativeInt(value).build()
  }

  @Before
  fun setUp() {
    setUpTestApplicationComponent()
  }

  private fun setUpTestApplicationComponent() {
    DaggerFractionInputIsExactlyEqualToRuleClassifierProviderTest_TestApplicationComponent
      .builder()
      .setApplication(ApplicationProvider.getApplicationContext()).build().inject(this)
  }

  // TODO(#89): Move to a common test library.
  private fun <T : Throwable> assertThrows(type: KClass<T>, operation: () -> Unit): T {
    try {
      operation()
      fail("Expected to encounter exception of $type")
    } catch (t: Throwable) {
      if (type.isInstance(t)) {
        return type.cast(t)
      }
      // Unexpected exception; throw it.
      throw t
    }
  }

  // TODO(#89): Move this to a common test application component.
  @Singleton
  @Component(modules = [])
  interface TestApplicationComponent {
    @Component.Builder
    interface Builder {
      @BindsInstance
      fun setApplication(application: Application): Builder

      fun build(): TestApplicationComponent
    }

    fun inject(test: FractionInputIsExactlyEqualToRuleClassifierProviderTest)
  }
}
