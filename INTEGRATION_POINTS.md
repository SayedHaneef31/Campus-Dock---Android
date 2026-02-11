# Integration Points - Upvote/Downvote Feature

## Component Interaction Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                     Fragment Layer                          │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │  AllPostsFragment / TrendingFragment                    │ │
│  │                                                         │ │
│  │  • Gets userId from TokenManager.getUserId()           │ │
│  │  • Creates PostAdapter with onVote callback            │ │
│  │  • onVote calls: viewModel.voteOnPost(...)             │ │
│  └──────────────┬──────────────────────────────────────────┘ │
└─────────────────┼──────────────────────────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────────────────────────┐
│                  ViewModel Layer                             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │  SocialPostsViewModel                                   │ │
│  │                                                         │ │
│  │  voteOnPost(postId, userId, voteType)                  │ │
│  │  └─> Launches coroutine on IO dispatcher               │ │
│  │  └─> Calls RetrofitClient.voteOnPost()                 │ │
│  │  └─> Handles response/errors                           │ │
│  └──────────────┬──────────────────────────────────────────┘ │
└─────────────────┼──────────────────────────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────────────────────────┐
│                  API Layer                                   │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │  ApiService.voteOnPost(postId, userId, voteType)       │ │
│  │                                                         │ │
│  │  @POST("/api/v1/posts/{postId}/vote")                  │ │
│  │  └─> Sends: POST with query params                     │ │
│  │  └─> Returns: Response<Void>                           │ │
│  └──────────────┬──────────────────────────────────────────┘ │
└─────────────────┼──────────────────────────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────────────────────────┐
│                 Backend (Spring Boot)                        │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │  PostController.voteOnPost(...)                         │ │
│  │                                                         │ │
│  │  Receives: postId, userId, voteType                    │ │
│  │  Business Logic:                                       │ │
│  │  ├─ Find post and user                                 │ │
│  │  ├─ Check if vote exists                               │ │
│  │  ├─ If exists & same type → Delete (toggle)            │ │
│  │  ├─ If exists & diff type → Update                     │ │
│  │  └─ If new → Create                                    │ │
│  │  Returns: 200 OK                                       │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

---

## File Dependencies

### Frontend Files
```
PostAdapter.kt
├─ Depends on: VoteType.kt
├─ Imports: com.sayed.campusdock.Data.Socials.VoteType
├─ Calls: onVote callback function
└─ Uses: RecyclerView, ImageView, TextView

AllPostsFragment.kt
├─ Depends on: PostAdapter.kt
├─ Depends on: SocialPostsViewModel.kt
├─ Depends on: TokenManager.kt (for userId)
├─ Creates: PostAdapter with vote callback
└─ Calls: viewModel.voteOnPost()

TrendingFragment.kt
├─ Same structure as AllPostsFragment
└─ Works with trending posts list

SocialPostsViewModel.kt
├─ Depends on: VoteType.kt
├─ Depends on: ApiService.kt
├─ Calls: RetrofitClient.instance.voteOnPost()
└─ Returns: Response<Void>

ApiService.kt
├─ Contains: voteOnPost endpoint definition
├─ Returns: Response<Void>
└─ Parameters: postId (Path), userId (Query), voteType (Query)
```

---

## Data Flow for Vote Action

### 1. User Clicks Upvote Button
```
PostAdapter.onBindViewHolder()
└─> ivUpvote.setOnClickListener {
    ├─> updateVoteUI(holder, VoteType.UPVOTE)  // Green icon
    ├─> onVote(post.id, userId, VoteType.UPVOTE)  // Callback
    }
```

### 2. Fragment Receives Vote Callback
```
AllPostsFragment.onCreateView()
└─> PostAdapter(
    posts,
    onVote = { postId, _, voteType ->
        viewModel.voteOnPost(postId, userId, voteType)
    }
)
```

### 3. ViewModel Processes Vote
```
SocialPostsViewModel.voteOnPost()
└─> viewModelScope.launch(Dispatchers.IO) {
    ├─> val response = RetrofitClient.instance.voteOnPost(
    │   postId, userId, voteType.name
    │)
    ├─> if (response.isSuccessful) { ... }
    └─> catch (t: Throwable) { ... }
}
```

### 4. API Call to Backend
```
GET Request:
  Method: POST
  URL: http://backend/api/v1/posts/{postId}/vote
  Query: ?userId={userId}&voteType=UPVOTE

Retrofit transforms to:
  @POST("api/v1/posts/{postId}/vote")
  suspend fun voteOnPost(
    @Path("postId") postId: UUID,
    @Query("userId") userId: UUID,
    @Query("voteType") voteType: String
  ): Response<Void>
```

### 5. Backend Response
```
✅ Success (200 OK)
   └─> Vote created/updated/deleted
   
❌ Error (400/401/500)
   └─> ViewModel catches and logs error
```

---

## Key Integration Points

### Point 1: TokenManager Integration
**Location**: AllPostsFragment.kt, TrendingFragment.kt
```kotlin
TokenManager.init(requireContext())
val userIdString = TokenManager.getUserId()
val userId = UUID.fromString(userIdString!!)
```
**Purpose**: Get current user ID for vote attribution

### Point 2: PostAdapter Vote Callback
**Location**: AllPostsFragment.kt, TrendingFragment.kt
```kotlin
PostAdapter(
    posts,
    onPostClick = { postId -> /* ... */ },
    onVote = { postId, _, voteType ->  // userId ignored, use from fragment
        viewModel.voteOnPost(postId, userId, voteType)
    }
)
```
**Purpose**: Connect adapter button clicks to ViewModel

### Point 3: ViewModel Vote Method
**Location**: SocialPostsViewModel.kt
```kotlin
fun voteOnPost(postId: UUID, userId: UUID, voteType: VoteType) {
    viewModelScope.launch(Dispatchers.IO) {
        val response = RetrofitClient.instance.voteOnPost(
            postId, userId, voteType.name
        )
    }
}
```
**Purpose**: Handle API call asynchronously

### Point 4: ApiService Endpoint
**Location**: ApiService.kt
```kotlin
@POST("api/v1/posts/{postId}/vote")
suspend fun voteOnPost(
    @Path("postId") postId: UUID,
    @Query("userId") userId: UUID,
    @Query("voteType") voteType: String
): Response<Void>
```
**Purpose**: Define API contract

### Point 5: UI Visual Feedback
**Location**: PostAdapter.kt
```kotlin
private fun updateVoteUI(holder: PostViewHolder, voteType: VoteType?) {
    when (voteType) {
        VoteType.UPVOTE -> holder.ivUpvote.setColorFilter(GREEN)
        VoteType.DOWNVOTE -> holder.ivDownvote.setColorFilter(RED)
        null -> { /* clear filters */ }
    }
}
```
**Purpose**: Provide immediate visual feedback

---

## Dependency Chain

```
UI Interaction
    ↓
PostAdapter.updateVoteUI()      ← Immediate visual feedback
    ↓
PostAdapter.onVote callback
    ↓
AllPostsFragment/TrendingFragment
    ↓
SocialPostsViewModel.voteOnPost()
    ↓
RetrofitClient.instance         ← HTTP client
    ↓
ApiService.voteOnPost()         ← API definition
    ↓
Retrofit (HTTP library)
    ↓
Backend API: POST /api/v1/posts/{postId}/vote
    ↓
Spring Boot PostController.voteOnPost()
    ↓
VoteService/VoteRepository
    ↓
Database: Create/Update/Delete Vote record
    ↓
Response: 200 OK
    ↓
ViewModel.errorMessage LiveData (on error)
```

---

## Error Handling Flow

```
ViewModel.voteOnPost()
    ↓
Try: Call API
    ├─ Success → errorMessage = null
    └─ Failure → errorMessage = error description
    ↓
Fragment.viewModel.errorMessage.observe()
    ├─ null → Hide error
    └─ error string → Show Toast/Snackbar
```

---

## Testing Integration Points

### Test 1: Vote Button Click
```kotlin
// Verify PostAdapter click listener works
ivUpvote.performClick()
// Assert: updateVoteUI called with UPVOTE
// Assert: onVote callback invoked
```

### Test 2: ViewModel Integration
```kotlin
// Verify ViewModel receives vote request
viewModel.voteOnPost(postId, userId, VoteType.UPVOTE)
// Assert: API call made with correct params
// Assert: Response handled properly
```

### Test 3: API Endpoint
```kotlin
// Verify ApiService.voteOnPost() signature
@POST("api/v1/posts/{postId}/vote")
suspend fun voteOnPost(
    @Path("postId") postId: UUID,
    @Query("userId") userId: UUID,
    @Query("voteType") voteType: String
): Response<Void>
// Assert: Matches backend endpoint exactly
```

---

## Configuration Notes

### VoteType Enum Naming
- Must match backend exactly: `UPVOTE`, `DOWNVOTE`
- Used in `voteType.name` conversion
- Passed as query parameter string

### UUID Handling
- All IDs (postId, userId, collegeId) are UUIDs
- TokenManager returns strings → converted to UUID.fromString()
- ApiService receives UUIDs → Retrofit converts to string in URL

### API Response
- Returns `Response<Void>` (empty response body)
- Check `response.isSuccessful` for success
- No data to parse on success

---

## Summary

The upvote/downvote feature is fully integrated through:
1. **UI Layer**: PostAdapter with vote buttons and visual feedback
2. **Logic Layer**: ViewModel handling vote requests asynchronously
3. **Network Layer**: ApiService defining the vote endpoint
4. **Data Layer**: TokenManager providing user context

All layers communicate through callbacks and LiveData, following Android best practices.
