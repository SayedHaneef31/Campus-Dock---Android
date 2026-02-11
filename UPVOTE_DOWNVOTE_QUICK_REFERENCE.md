# Quick Reference: Upvote/Downvote Implementation

## Summary of Changes

### New Files Created:
1. `com/sayed/campusdock/Data/Socials/VoteType.kt` - Enum for vote types
2. `com/sayed/campusdock/Data/Socials/Vote.kt` - Vote data class

### Modified Files:

#### 1. ApiService.kt
```kotlin
@POST("api/v1/posts/{postId}/vote")
suspend fun voteOnPost(
    @Path("postId") postId: UUID,
    @Query("userId") userId: UUID,
    @Query("voteType") voteType: String
): Response<Void>
```

#### 2. SocialPostsViewModel.kt
Added method:
```kotlin
fun voteOnPost(postId: UUID, userId: UUID, voteType: VoteType) {
    // Makes API call to vote on post
}
```

#### 3. PostAdapter.kt
- Updated constructor to accept `onVote` callback
- Added vote button click listeners
- Added `currentVoteState` to track user's vote
- Added `updateVoteUI()` method for visual feedback

#### 4. AllPostsFragment.kt & TrendingFragment.kt
- Now retrieve `userId` from TokenManager
- Pass `userId` and `voteType` callbacks to adapter
- Adapter calls ViewModel to process votes

#### 5. socials_item_post.xml
- Wrapped vote buttons in clickable containers
- Added ripple effect and proper padding
- Better visual feedback for user interactions

## Code Example: How to Use

In a fragment using the PostAdapter:

```kotlin
val userId = UUID.fromString(TokenManager.getUserId()!!)

postAdapter = PostAdapter(
    posts,
    onPostClick = { postId ->
        // Navigate to post details
        findNavController().navigate(...)
    },
    onVote = { postId, _, voteType ->
        // Vote gets sent to backend
        viewModel.voteOnPost(postId, userId, voteType)
    }
)
```

## Vote API Endpoint

**Request:**
```
POST /api/v1/posts/{postId}/vote?userId={userId}&voteType={UPVOTE|DOWNVOTE}
```

**Response:** 200 OK (Void)

**Backend Logic:**
- If new vote → Create vote record
- If same vote type → Delete vote record (toggle off)  
- If different vote type → Update vote record

## Visual States

| State | Icon Color | Meaning |
|-------|-----------|---------|
| No Vote | Gray (default) | User hasn't voted |
| Upvote | Green | User upvoted |
| Downvote | Red | User downvoted |

## Debugging Tips

1. Check TokenManager has `getUserId()` returning valid UUID
2. Verify API endpoint matches backend exactly
3. Watch network tab for vote API calls
4. Check logcat for vote errors
5. Ensure vote buttons have proper click listeners set

## Known Considerations

- Vote state is UI-only (not persisted locally)
- API calls happen asynchronously
- Visual feedback is immediate, API response is async
- Both AllPostsFragment and TrendingFragment support voting
- Each post tracks its own vote state independently
