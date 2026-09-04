package com.mineinspect.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.*

/** Primary Safety Action button — bg Primary, 52-56dp height. */
@Composable
fun PrimaryActionButton(
    text: String,
    modifier: Modifier = Modifier,
    height: Dp = Dimens.touchLarge,
    loading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(Dimens.radiusMd),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = OnPrimary,
            disabledContainerColor = Primary.copy(alpha = 0.6f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = OnPrimary,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(8.dp))
        } else {
            leadingIcon?.invoke()
        }
        Text(text, style = AppType.labelLg)
        trailingIcon?.invoke()
    }
}

/** Secondary/outline button — light fill + border. */
@Composable
fun SecondaryActionButton(
    text: String,
    modifier: Modifier = Modifier,
    height: Dp = Dimens.touchLarge,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(Dimens.radiusMd),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SurfaceContainerLow,
            contentColor = OnSurface
        ),
        border = BorderStroke(1.dp, OutlineVariant)
    ) {
        leadingIcon?.invoke()
        Text(text, style = AppType.labelLg)
    }
}

/** Destructive/Halt action — red for hazard reporting. */
@Composable
fun DestructiveActionButton(
    text: String,
    modifier: Modifier = Modifier,
    height: Dp = Dimens.touchLarge,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(Dimens.radiusMd),
        colors = ButtonDefaults.buttonColors(
            containerColor = ErrorColor,
            contentColor = OnError
        )
    ) {
        leadingIcon?.invoke()
        Text(text, style = AppType.labelLg)
    }
}
