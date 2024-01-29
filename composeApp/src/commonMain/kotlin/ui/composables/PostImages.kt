package pizza.xyz.befake.ui.composables

import VideoPlayer
import VideoPlayerState
import androidx.compose.animation.core.animate
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import getScreenSize
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.utils.Utils
import ui.composables.PostLoading
import kotlin.math.roundToInt


enum class PostImageState {
    INTERACTABLE,
    STATIC
}

@Composable
fun PostImagesV2(
    post: Posts,
    showForeground: Boolean,
    changeShowForeground: (Boolean) -> Unit,
    state: PostImageState,
    height: Dp,
) {
    val borderMarginV2 by remember(height) { mutableFloatStateOf((height.value * 0.1).coerceIn(10.0, 55.0).toFloat()) }
    val cornerRadiusV2 by remember(height) { mutableFloatStateOf((height.value * 0.03).coerceIn(5.0, 16.5).toFloat()) }
    val borderStroke by remember(height) { mutableFloatStateOf((height.value * 0.0036).coerceIn(1.0, 2.0).toFloat()) }

    val coroutineScope = rememberCoroutineScope()
    var outerBoxSize by remember { mutableStateOf(Offset(0f, 0f)) }
    val haptic = LocalHapticFeedback.current
    val primary = asyncPainterResource(post.primary.url)
    val secondary = asyncPainterResource(post.secondary.url)
    var showPrimaryAsMain by remember {
        mutableStateOf(true)
    }

    var playerState by remember {
        mutableStateOf(VideoPlayerState.Ended)
    }

    Box(
        modifier = Modifier
            .width((height.value * 0.75).dp)
            .clip(RoundedCornerShape(cornerRadiusV2.dp))
            .onSizeChanged {
                outerBoxSize = Offset(it.width.toFloat(), it.height.toFloat())
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        if (state != PostImageState.INTERACTABLE) return@detectDragGesturesAfterLongPress
                        changeShowForeground(false)
                        playerState = VideoPlayerState.Playing
                    },
                    onDragEnd = {
                        if (state != PostImageState.INTERACTABLE) return@detectDragGesturesAfterLongPress
                        if (post.postType != "bts")
                            changeShowForeground(true)
                        else
                            playerState = VideoPlayerState.Ended
                    },
                    onDrag = { _, _ -> }
                )
            }
    ) {
        var offsetX by remember { mutableFloatStateOf(borderMarginV2) }
        var offsetY by remember { mutableFloatStateOf(borderMarginV2) }

        var oldHeight by remember {
            mutableStateOf(height)
        }

        LaunchedEffect(height) {

            if (oldHeight == height) return@LaunchedEffect

            offsetY = ((offsetY)/oldHeight.value * height.value)
            offsetX = ((offsetX)/(oldHeight.value * 0.75f) * (height.value * 0.75f))

            oldHeight = height
        }

        if (!showForeground && (post.postType == "bts" && post.btsMedia != null) && state == PostImageState.INTERACTABLE) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            VideoPlayer(
                modifier = Modifier
                    .height(height)
                    .width(getScreenSize().first.dp)
                    .clip(RoundedCornerShape(cornerRadiusV2.dp)),
                url = post.btsMedia.url,
                state = playerState,
            ) {
                changeShowForeground(true)
            }
        } else {
            when (val painter = if (showPrimaryAsMain) primary else secondary) {
                is Resource.Success -> {
                    KamelImage(
                        resource = painter,
                        contentDescription = "realmoji",
                    )
                }
                else -> PostLoading()
            }
        }

        if (showForeground) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .height((height.value * 0.3).dp)
                    .width(((height.value * 0.75) * 0.3).dp)
                    .align(Alignment.TopStart)
                    .pointerInput(Unit) {
                        val boxSize = this.size
                        detectDragGestures(onDragEnd = {
                            coroutineScope.launch {

                                val targetValWidth = if (offsetX > (outerBoxSize.x) / 3) {
                                    outerBoxSize.x - (boxSize.width.toFloat() + borderMarginV2)
                                } else {
                                    borderMarginV2
                                }

                                val jobX = async {
                                    animate(offsetX, targetValWidth) { it, _ ->
                                        offsetX = it
                                    }
                                }

                                val jobY = async {
                                    animate(offsetY, borderMarginV2) { it, _ ->
                                        offsetY = it
                                    }
                                }

                                jobX.await()
                                jobY.await()
                            }
                        }) { _, dragAmount ->
                            offsetX = (offsetX + dragAmount.x).coerceIn(
                                borderMarginV2,
                                outerBoxSize.x - (boxSize.width + borderMarginV2)
                            )
                            offsetY = (offsetY + dragAmount.y).coerceIn(
                                borderMarginV2,
                                outerBoxSize.y - (boxSize.height + borderMarginV2)
                            )
                        }
                    }
            ) {
                Spacer(modifier = Modifier.fillMaxSize())

                when (val painter = if (!showPrimaryAsMain) primary else secondary) {
                    is Resource.Success -> {
                        KamelImage(
                            modifier = Modifier
                                .clip(RoundedCornerShape(cornerRadiusV2.dp))
                                .border(borderStroke.dp, Color.Black, RoundedCornerShape(cornerRadiusV2.dp))
                                .clickable {
                                    if (state != PostImageState.INTERACTABLE) return@clickable
                                    showPrimaryAsMain = !showPrimaryAsMain
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                            resource = painter,
                            contentDescription = "realmoji",
                        )
                    }
                    else -> { }
                }
            }
        }
    }
}

//@Preview
@Composable
fun PostImagesPreviewINTERACTABLE() {
    var showForeground by remember { mutableStateOf(true) }
    PostImagesV2(
        post = Utils.testFeedPostNoLocation,
        state = PostImageState.INTERACTABLE,
        height = 550f.dp,
        showForeground = showForeground,
        changeShowForeground = { showForeground = it },
    )
}

//@Preview
@Composable
fun PostImagesPreviewStatic() {
    var showForeground by remember { mutableStateOf(true) }
    PostImagesV2(
        post = Utils.testFeedPostNoLocation,
        state = PostImageState.STATIC,
        height = 200f.dp,
        showForeground = showForeground,
        changeShowForeground = {showForeground = it},
    )
}
