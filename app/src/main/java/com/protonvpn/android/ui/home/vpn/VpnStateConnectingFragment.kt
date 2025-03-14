/*
 * Copyright (c) 2021. Proton Technologies AG
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

package com.protonvpn.android.ui.home.vpn

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.protonvpn.android.R
import com.protonvpn.android.components.VpnUiDelegateProvider
import com.protonvpn.android.databinding.FragmentVpnStateConnectingBinding
import com.protonvpn.android.vpn.DisconnectTrigger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class VpnStateConnectingFragment : Fragment(R.layout.fragment_vpn_state_connecting) {

    private val parentViewModel: VpnStateViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val changeServerViewModel: ChangeServerViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentVpnStateConnectingBinding.bind(view)
        with(binding) {
            buttonCancel.setOnClickListener {
                parentViewModel.disconnectAndClose(DisconnectTrigger.ConnectionPanel("connection panel"))
            }
            buttonChangeServer.setOnClickListener {
                changeServerViewModel.changeServer(
                    (requireActivity() as VpnUiDelegateProvider).getVpnUiDelegate(),
                    dontUpdateCounters = true
                )
            }
            changeServerViewModel.hasTroubleConnecting
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .onEach { hasTroubleConnecting -> buttonChangeServer.isVisible = hasTroubleConnecting }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }
}
