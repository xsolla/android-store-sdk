package com.xsolla.android.storesdkexample.ui.fragments.friends

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequestAction
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.AddFriendsAdapter
import com.xsolla.android.storesdkexample.adapter.SocialFriendsAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmAddFriends
import com.xsolla.android.storesdkexample.ui.vm.VmSocialFriends
import com.xsolla.android.storesdkexample.util.setRateLimitedClickListener
import kotlinx.android.synthetic.main.fragment_add_friends.*

class AddFriendsFragment : BaseFragment() {

    private companion object {
        private const val RC_LINKING = 33
    }

    private lateinit var socialNetworksIcons: Map<SocialNetworkForLinking, Triple<Int, Int, ImageView>>

    private val vmAddFriends: VmAddFriends by viewModels()
    private val vmSocialFriends: VmSocialFriends by viewModels()

    private lateinit var searchAdapter: AddFriendsAdapter
    private lateinit var socialFriendsAdapter: SocialFriendsAdapter

    override fun getLayout() = R.layout.fragment_add_friends

    override val toolbarOption = ToolbarOptions(showBalance = false, showCart = false)

    override fun initUI() {
        socialNetworksIcons = mapOf(
                SocialNetworkForLinking.FACEBOOK to Triple(R.drawable.ic_linking_facebook_add, R.drawable.ic_linking_facebook_added, iconFacebook),
                SocialNetworkForLinking.VK to Triple(R.drawable.ic_linking_vk_add, R.drawable.ic_linking_vk_added, iconVk),
                SocialNetworkForLinking.TWITTER to Triple(R.drawable.ic_linking_twitter_add, R.drawable.ic_linking_twitter_added, iconTwitter)
        )
        searchAdapter = AddFriendsAdapter(
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REMOVE) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.BLOCK) },
                {},
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_APPROVE) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_DENY) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_CANCEL) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_ADD) }
        )
        socialFriendsAdapter = SocialFriendsAdapter(
                { vmSocialFriends.updateFriendship(it.toFriendUiEntity(), UpdateUserFriendsRequestAction.FRIEND_REMOVE) },
                { vmSocialFriends.updateFriendship(it.toFriendUiEntity(), UpdateUserFriendsRequestAction.BLOCK) },
                {},
                { vmSocialFriends.updateFriendship(it.toFriendUiEntity(), UpdateUserFriendsRequestAction.FRIEND_REQUEST_APPROVE) },
                { vmSocialFriends.updateFriendship(it.toFriendUiEntity(), UpdateUserFriendsRequestAction.FRIEND_REQUEST_DENY) },
                { vmSocialFriends.updateFriendship(it.toFriendUiEntity(), UpdateUserFriendsRequestAction.FRIEND_REQUEST_CANCEL) },
                { vmSocialFriends.updateFriendship(it.toFriendUiEntity(), UpdateUserFriendsRequestAction.FRIEND_REQUEST_ADD) }
        )
        searchInput.addTextChangedListener {
            vmAddFriends.currentSearchQuery.value = it?.toString() ?: ""
        }
        vmSocialFriends.loadLinkedSocialAccounts()
        vmSocialFriends.loadAllSocialFriends()
        vmSocialFriends.linkedSocialNetworks.observe(viewLifecycleOwner) {
            initSocialButtons(it)
        }
        vmAddFriends.searchResultList.observe(viewLifecycleOwner) { list ->
            searchAdapter.submitList(list)
            setEmptyTextVisibility()
        }
        vmSocialFriends.socialFriendsList.observe(viewLifecycleOwner) { list ->
            socialFriendsAdapter.submitList(list)
            setEmptyTextVisibility()
        }
        vmAddFriends.currentSearchQuery.observe(viewLifecycleOwner) { query ->
            if (query.isNullOrBlank()) {
                initSocialScreen()
            } else {
                initSearchScreen()
            }
        }
        initSocialScreen()
        val errorObserver = { errorMessage: String ->
            showSnack(errorMessage)
        }
        vmAddFriends.hasError.observe(viewLifecycleOwner, errorObserver)
        vmSocialFriends.hasError.observe(viewLifecycleOwner, errorObserver)
        vmAddFriends.loadAllFriends()

        updateFriendsButton.setOnClickListener { vmSocialFriends.updateSocialFriends() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LINKING) {
            if (resultCode == Activity.RESULT_OK) {
                vmSocialFriends.loadLinkedSocialAccounts()
                vmSocialFriends.updateSocialFriends()
            } else {
                showSnack(getString(R.string.add_friends_linking_error))
            }
        }
    }

    private fun initSocialButtons(list: List<SocialNetworkForLinking?>) {
        for (socialNetwork in SocialNetworkForLinking.values()) {
            val info = socialNetworksIcons.getValue(socialNetwork)
            if (socialNetwork in list) {
                info.third.setRateLimitedClickListener { }
                info.third.setImageResource(info.second)
            } else {
                info.third.setRateLimitedClickListener {
                    startActivityForResult(
                            XLogin.createSocialAccountLinkingIntent(requireContext(), socialNetwork),
                            RC_LINKING
                    )
                }
                info.third.setImageResource(info.first)
            }
        }
    }

    private fun initSocialScreen() {
        labelSocialAccounts.text = getString(R.string.add_friends_social_accounts)
        labelSocialAccounts.isVisible = true
        socialButtonsScroll.isVisible = true
        labelListTitle.text = getString(R.string.add_friends_recommended)
        labelListTitle.isVisible = true
        updateFriendsButton.isVisible = true
        recycler.adapter = socialFriendsAdapter
        recyclerEmpty.text = getString(R.string.add_friends_social_empty)
        setEmptyTextVisibility()
    }

    private fun initSearchScreen() {
        labelSocialAccounts.isGone = true
        socialButtonsScroll.isGone = true
        labelListTitle.isGone = true
        updateFriendsButton.isGone = true
        recycler.adapter = searchAdapter
        recyclerEmpty.text = getString(R.string.add_friends_search_empty)
        setEmptyTextVisibility()
    }

    private fun setEmptyTextVisibility() {
        recyclerEmpty.isVisible =
                vmAddFriends.currentSearchQuery.value.isNullOrBlank() &&
                        vmSocialFriends.socialFriendsList.value.isNullOrEmpty() ||
                        !vmAddFriends.currentSearchQuery.value.isNullOrBlank() &&
                        vmAddFriends.searchResultList.value.isNullOrEmpty()
    }

}