# Upvote/Downvote Implementation - Frontend

## Overview
This document outlines the implementation of the upvote/downvote feature for posts on the Android frontend.

## Files Modified/Created

### 1. **Data Classes**
- **`VoteType.kt`** (NEW)
  - Enum with `UPVOTE` and `DOWNVOTE` values
  - Matches the backend enum structure
  
- **`Vote.kt`** (NEW)
  - Data class representing a vote object
  - Contains: `id`, `postId`, `userId`, `voteType`

### 2. **API Service**
- **`ApiService.kt`** (MODIFIED)
  - Added new endpoint:
    ```kotlin
    @POST("api/v1/posts/{postId}/vote")
    suspend fun voteOnPost(
        @Path("postId") postId: UUID,
        @Query("userId") userId: UUID,
        @Query("voteType") voteType: String
    ): Response<Void>
    ```
  - Sends upvote/downvote requests to the backend

### 3. **ViewModel**
- **`SocialPostsViewModel.kt`** (MODIFIED)
  - Added new method `voteOnPost(postId, userId, voteType)`
  - Handles API call to vote on a post
  - Runs on IO dispatcher for non-blocking operations
  - Error handling with LiveData `errorMessage`

### 4. **Adapter**
- **`PostAdapter.kt`** (MODIFIED)
  - Constructor now accepts:
    - `onPostClick`: Callback for post clicks
    - `onVote`: Callback for vote actions with userId and voteType
  
  - Added `currentVoteState` tracking in `PostViewHolder`
    - Tracks whether user has upvoted or downvoted
  
  - Click listeners for vote buttons:
    - Upvote: Toggles between upvote/no-vote states
    - Downvote: Toggles between downvote/no-vote states
  
  - `updateVoteUI()` helper method:
    - Changes icon color based on vote state
    - Green for upvote, Red for downvote, Gray for no vote

### 5. **Fragments**
- **`AllPostsFragment.kt`** (MODIFIED)
  - Retrieves both `collegeId` and `userId` from TokenManager
  - Passes both to the PostAdapter via callbacks
  - onVote lambda calls `viewModel.voteOnPost()`

- **`TrendingFragment.kt`** (MODIFIED)
  - Similar changes as AllPostsFragment
  - Ensures consistency across all post lists

### 6. **Layout**
- **`socials_item_post.xml`** (MODIFIED)
  - Wrapped vote buttons in clickable containers with:
    - `?attr/selectableItemBackground` for ripple effect
    - Padding for better touch targets
    - Added `contentDescription` for accessibility
  - Vote buttons now visually responsive with feedback

## How It Works

### Flow Diagram
```
User taps Upvote/Downvote button
         ↓
PostAdapter click listener triggered
         ↓
updateVoteUI() changes icon color (visual feedback)
         ↓
onVote callback invoked with postId, userId, voteType
         ↓
AllPostsFragment/TrendingFragment calls viewModel.voteOnPost()
         ↓
ViewModel calls RetrofitClient.voteOnPost() (API call)
         ↓
Backend processes vote:
  - If new vote: creates Vote record
  - If same vote: removes Vote record (toggle off)
  - If different vote: updates Vote record
         ↓
Response returned to ViewModel
```

### Vote States
1. **No Vote**: Icon shows in gray, count displays normally
2. **Upvote**: Icon tints green, indicates user upvoted
3. **Downvote**: Icon tints red, indicates user downvoted

### Toggle Behavior
- **Same vote clicked again**: Vote is removed (toggle off)
- **Different vote clicked**: Vote is changed and updated
- Non-blocking: UI updates immediately, API call happens in background

## Integration Points

### TokenManager
- `getCollegeId()`: Gets the college ID for filtering posts
- `getUserId()`: Gets the current user ID for vote attribution

### Key Features
✅ Visual feedback on vote buttons  
✅ Real-time UI updates  
✅ Toggle vote on/off by re-clicking  
✅ Proper API integration with backend  
✅ Error handling and loading states  
✅ Works for both "All Posts" and "Trending" tabs  

## Testing Checklist
- [ ] Tap upvote button → icon turns green
- [ ] Tap upvote again → icon turns gray (vote removed)
- [ ] Tap downvote button → icon turns red
- [ ] Switch from upvote to downvote → icon changes from green to red
- [ ] Check network tab for vote API calls
- [ ] Verify vote count increments/decrements
- [ ] Test on both AllPostsFragment and TrendingFragment

## Future Enhancements
- Store vote state locally to show user's vote without API call
- Add animation when toggling votes
- Show which users voted on a post
- Real-time vote count updates via WebSocket
- Persist vote state across app restarts
