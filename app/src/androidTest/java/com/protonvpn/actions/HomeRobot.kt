/*
 *  Copyright (c) 2021 Proton AG
 *
 * This file is part of ProtonVPN.
 *
 * ProtonVPN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProtonVPN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProtonVPN.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.protonvpn.actions

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.protonvpn.TestSettings
import com.protonvpn.android.R
import com.protonvpn.android.models.vpn.Partner
import com.protonvpn.android.models.vpn.PartnerType
import com.protonvpn.android.utils.Constants
import com.protonvpn.base.BaseRobot
import com.protonvpn.base.BaseVerify
import com.protonvpn.matchers.ProtonMatcher.lastChild
import com.protonvpn.testsHelper.ServiceTestHelper
import me.proton.core.test.android.instrumented.ui.espresso.OnView
import me.proton.core.test.android.robots.auth.AddAccountRobot
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import me.proton.test.fusion.Fusion.node

/**
 * [HomeRobot] Contains all actions and verifications for home screen
 */
class HomeRobot : BaseRobot() {

    fun openAccountView(): AccountRobot = selectDrawerMenuOption(R.id.layoutUserInfo)

    fun logout(): AddAccountRobot = selectDrawerMenuOption(R.id.drawerButtonLogout)

    fun clickOnDrawerMenuAccountOption(): AccountRobot =
        selectDrawerMenuOption(R.id.drawerButtonAccount)

    fun clickOnDrawerMenuReportBugOption(): BugReportRobot =
        selectDrawerMenuOption(R.id.drawerButtonReportBug)

    fun clickOnDrawerMenuShowLogOption(): HomeRobot =
        selectDrawerMenuOption(R.id.drawerButtonShowLog)

    fun clickOnDrawerMenuHelpOption(): HomeRobot =
        selectDrawerMenuOption(R.id.drawerButtonHelp)

    fun logoutAfterWarning(): AddAccountRobot =
        clickElementByText(R.string.logoutConfirmDialogButton)

    fun clickOnInformationIcon(): HomeRobot = clickElementById(R.id.action_info)

    fun clickCancel(): HomeRobot = clickElementByText(R.string.cancel)

    fun swipeDownToCloseConnectionInfoLayout(): HomeRobot =
        swipeDownOnElementById(R.id.layoutBottomSheet)

    fun connectViaFastest(): HomeRobot = clickElementById(R.id.buttonConnect)

    fun pressGotIt(): HomeRobot = clickElementByText(R.string.got_it)

    fun changeServer(): HomeRobot = clickElementById(R.id.composeChangeServer)

    fun clickPlusLocationUpsell(): HomeRobot {
        val upsellString = InstrumentationRegistry.getInstrumentation().targetContext.getString(
            R.string.free_upsell_header_title
        )
        clickElementByText<HomeRobot>(upsellString)
        return this
    }

    fun swipeLeftToOpenMap(): MapRobot {
        swipeLeftOnElementById<MapRobot>(R.id.list)
        return waitUntilDisplayed(R.id.mapView)
    }

    fun swipeDownToRefreshServerList(): HomeRobot = swipeDownOnElementById(R.id.list)

    fun setStateOfSecureCoreSwitch(state: Boolean): HomeRobot {
        if (state != ServiceTestHelper().isSecureCoreEnabled)
            clickElementById<HomeRobot>(R.id.switchSecureCore)
        return this
    }

    fun disableDontShowAgain(): HomeRobot {
        view.withText(R.string.dialogDontShowAgain).isChecked().click()
        return this
    }

    fun acceptSecureCoreInfoDialog(): HomeRobot =
        clickElementByText(R.string.secureCoreActivateDialogButton)

    fun swipeLeftToOpenProfiles(): ProfilesRobot {
        view.waitForCondition {
            view.withId(R.id.coordinator).swipeLeft()
            Espresso.onView(ViewMatchers.withId(R.id.textCreateProfile))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
        return ProfilesRobot()
    }

    fun connectThroughQuickConnect(profileName: String): ConnectionRobot {
        longClickByCustomMatcher<HomeRobot>(
            lastChild(
                ViewMatchers.withId(R.id.fabQuickConnect),
                withClassName(Matchers.endsWith("FloatingActionButton"))
            )
        )
        clickElementByText<HomeRobot>(profileName)
        if (!TestSettings.mockedConnectionUsed) {
            allowVpnToBeUsed()
        }
        return ConnectionRobot()
    }

    fun allowVpnToBeUsed() {
        if (isAllowVpnRequestVisible()) {
            device.clickNotificationByText("OK")
        }
    }

    fun scrollUpToTheLogs() {
        repeat(3) {
            swipeDownOnElementById<HomeRobot>(R.id.recyclerItems)
        }
    }

    fun openPartnersInfo(country: String): HomeRobot {
        clickElementByText<HomeRobot>(country)
        return clickElementById(R.id.serversInfo)
    }

    fun openPartnersInfoUsingLogo(country: String, server: String, partnerName: String): HomeRobot {
        clickElementByText<HomeRobot>(country)
        inServerRow(server).withContentDesc(partnerName).click()
        return this
    }

    fun clickUpgradeByCountryName(country: String) {
        view.withId(R.id.buttonUpgrade).hasSibling(view.withText(country)).click()
    }

    private fun isAllowVpnRequestVisible(): Boolean {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        return uiDevice.findObject(UiSelector().textContains("Connection request")).exists()
    }

    private inline fun <reified T> selectDrawerMenuOption(@IdRes drawerMenuOptionId: Int): T {
        clickElementByContentDescription<HomeRobot>(R.string.hamburgerMenu)
        return clickElementById(drawerMenuOptionId)
    }

    private fun inServerRow(serverName: String, fastest: Boolean = false): OnView {
        val fastestMatcher =
            if (fastest) withId(R.id.fastest)
            else not(withId(R.id.fastest))
        return view.withAncestor(
            view.hasSibling(view.withChild(view.withText(serverName)))
                .withParent(view.withCustomMatcher(fastestMatcher))
        )
    }

    class Verify : BaseVerify() {
        fun isInMainScreen() = checkIfElementIsDisplayedById(R.id.fabQuickConnect)

        fun serverListIsVisible() = checkIfElementIsDisplayedById(R.id.list)

        fun dialogUpgradeVisible() {
            checkIfElementIsDisplayedByStringId(R.string.upgrade_secure_core_title)
        }

        fun dialogSpeedInfoVisible() {
            checkIfElementIsDisplayedByStringId(R.string.secureCoreSpeedInfoTitle)
        }

        fun dialogSpeedInfoNotVisible() {
            checkIfElementIsNotDisplayedByStringId(R.string.secureCoreSpeedInfoTitle)
        }

        fun isSecureCoreDisabled() {
            assertFalse(ServiceTestHelper().isSecureCoreEnabled)
        }

        fun loginScreenIsNotDisplayed() {
            checkIfElementDoesNotExistById(R.id.sign_in)
            checkIfElementDoesNotExistById(R.id.sign_up)
        }

        fun warningMessageIsDisplayed() {
            checkIfElementIsDisplayedByStringId(R.string.logoutConfirmDialogTitle)
            checkIfElementIsDisplayedByStringId(R.string.logoutConfirmDialogMessage)
        }

        fun assertThatSecureCoreSwitchIsDisabled() {
            checkIfElementIsNotChecked(R.id.switchSecureCore)
            assertFalse(ServiceTestHelper().isSecureCoreEnabled)
        }

        fun assertThatSecureCoreSwitchIsEnabled() {
            checkIfElementIsChecked(R.id.switchSecureCore)
            assertTrue(ServiceTestHelper().isSecureCoreEnabled)
        }

        fun assertThatInLogsScreen() {
            checkIfElementIsDisplayedByStringId(R.string.log_title)
        }

        fun helpOptionOpensUrl() {
            Intents.intended(IntentMatchers.hasData("https://protonvpn.com/support?utm_source=" + Constants.PROTON_URL_UTM_SOURCE))
        }

        fun serverInfoLegendDescribesAllServerTypes() {
            checkIfElementIsDisplayedByStringId(R.string.info_features)
            checkIfElementIsDisplayedByStringId(R.string.smart_routing_title)
            checkIfElementIsDisplayedByStringId(R.string.smart_routing_description)
            checkIfElementIsDisplayedByStringId(R.string.streaming_title)
            checkIfElementIsDisplayedByStringId(R.string.streaming_description)
            checkIfElementIsDisplayedByStringId(R.string.p2p_title)
            checkIfElementIsDisplayedByStringId(R.string.p2p_description)
            checkIfElementIsDisplayedByStringId(R.string.tor_title)
            checkIfElementIsDisplayedByStringId(R.string.tor_description)
            checkIfElementIsDisplayedByStringId(R.string.info_performance)
            checkIfElementIsDisplayedByStringId(R.string.server_load_title)
            checkIfElementIsDisplayedByStringId(R.string.server_load_description)
        }

        fun checkIfPartnersDataIsDisplayedProperly(partner: Partner) {
            checkIfElementIsDisplayedByText(partner.description!!)
            checkIfElementIsDisplayedByText(partner.name!!)
        }

        fun checkIfPartnerIsNotDisplayed(partner: Partner) {
            checkIfElementIsNotDisplayedByText(partner.description!!)
            checkIfElementIsNotDisplayedByText(partner.name!!)
        }

        fun checkIfPartnerTypeIsShown(partnerType: PartnerType) {
            checkIfElementIsDisplayedByText(partnerType.type!!)
            checkIfElementIsDisplayedByText(partnerType.description!!)
        }

        fun changeServerAndDisconnectIsDisplayed() {
            checkIfElementIsDisplayedById(R.id.composeChangeServer)
            checkIfElementIsDisplayedByStringId(R.string.disconnect)
        }

        fun changeServerTimerAndBannerAreDisplayed() {
            node.withText(R.string.not_wanted_country_title).assertIsDisplayed()
            node.withText(R.string.not_wanted_country_description).assertIsDisplayed()
            node.useUnmergedTree().withTag("remainingTimeRow").assertIsDisplayed()
        }

        fun cooldownUpgradeIsShown(){
            node.withText(R.string.upgrade).assertIsDisplayed()
            node.withText(R.string.server_change_upsell).assertIsDisplayed()
        }
    }

    inline fun verify(block: Verify.() -> Unit) = Verify().apply(block)
}
