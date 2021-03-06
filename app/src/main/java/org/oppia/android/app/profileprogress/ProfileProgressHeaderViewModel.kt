package org.oppia.android.app.profileprogress

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import org.oppia.android.app.home.RouteToRecentlyPlayedListener
import org.oppia.android.app.model.Profile

/** Header [ViewModel] for the recycler view in [ProfileProgressFragment]. */
class ProfileProgressHeaderViewModel(activity: AppCompatActivity, fragment: Fragment) :
  ProfileProgressItemViewModel() {
  private val routeToCompletedStoryListListener = activity as RouteToCompletedStoryListListener
  private val routeToOngoingTopicListListener = activity as RouteToOngoingTopicListListener
  private val routeToRecentlyPlayedActivity = activity as RouteToRecentlyPlayedListener
  private val profilePictureEditListener = fragment as ProfilePictureClickListener

  val profile = ObservableField<Profile>(Profile.getDefaultInstance())
  val ongoingTopicCount = ObservableField(0)
  val completedStoryCount = ObservableField(0)

  fun setProfile(currentProfile: Profile) {
    profile.set(currentProfile)
  }

  fun setOngoingTopicCount(topicCount: Int) {
    ongoingTopicCount.set(topicCount)
  }

  fun setCompletedStoryCount(storyCount: Int) {
    completedStoryCount.set(storyCount)
  }

  fun clickOnCompletedStoryCount() {
    routeToCompletedStoryListListener.routeToCompletedStory()
  }

  fun clickOnOngoingTopicCount() {
    routeToOngoingTopicListListener.routeToOngoingTopic()
  }

  fun clickOnViewAll() {
    routeToRecentlyPlayedActivity.routeToRecentlyPlayed()
  }

  fun clickOnProfilePicture() {
    profilePictureEditListener.onProfilePictureClicked()
  }
}
