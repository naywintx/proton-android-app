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

package com.protonvpn.testsHelper

import com.protonvpn.android.BuildConfig
import com.protonvpn.android.ui.onboarding.OnboardingPreferences
import com.protonvpn.android.utils.Storage
import me.proton.core.test.android.plugins.Quark
import me.proton.core.util.kotlin.takeIfNotBlank
import me.proton.core.util.kotlin.takeIfNotEmpty

class TestSetup {
    companion object {

        val quark: Quark? = BuildConfig.BLACK_TOKEN.takeIfNotBlank()?.let {
            Quark(BuildConfig.API_DOMAIN, BuildConfig.BLACK_TOKEN, "sensitive/internal_apis.json")
        }

        fun setCompletedOnboarding() {
            //set flag to slide show to be visible
            Storage.saveBoolean(OnboardingPreferences.MAPVIEW_DIALOG, true)
            Storage.saveBoolean(OnboardingPreferences.PROFILES_DIALOG, true)
            Storage.saveBoolean(OnboardingPreferences.FLOATINGACTION_DIALOG, true)
            Storage.saveBoolean(OnboardingPreferences.FLOATING_BUTTON_USED, true)
            Storage.saveBoolean(OnboardingPreferences.NETSHIELD_DIALOG, true)
        }
    }
}
