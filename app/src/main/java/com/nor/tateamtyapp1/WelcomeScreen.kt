package com.nor.tateamtyapp1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val primaryNavy = Color(0xFF002D5A)
private val accentGreen = Color(0xFF2E7D32)
private val lightBlueButton = Color(0xFFE1F5FE) // لون أزرق فاتح جداً للأزرار

@Composable
fun WelcomeScreen(
    onStaffLoginClicked: () -> Unit,
    onParentPortalClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branding Phrase at the top - Made larger and Black
            Text(
                text = "المنظومة الذكية لإدارة التحصينات",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1. Welcome Header - Scaled Up
            Text(
                text = "Welcome to Tateamty",
                color = primaryNavy,
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Slogans - Scaled Up
            Text(
                text = "صحة طفلك تبدأ من هنا",
                color = accentGreen,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Your child's health journey begins here",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Central Illustration (Circular)
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .border(3.dp, lightBlueButton, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.welcome_image),
                    contentDescription = "Welcome Illustration",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Action Buttons with Light Blue Background for clarity
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Parent Portal Button
                CustomWelcomeButtonLarge(
                    text = "بوابة ولي الأمر - Parent Portal",
                    icon = Icons.Default.FamilyRestroom,
                    containerColor = lightBlueButton,
                    contentColor = Color.Black,
                    onClick = onParentPortalClicked
                )

                // Staff Login Button
                CustomWelcomeButtonLarge(
                    text = "دخول الموظف - Staff Login",
                    icon = Icons.Default.Badge,
                    containerColor = lightBlueButton,
                    contentColor = Color.Black,
                    onClick = onStaffLoginClicked
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CustomWelcomeButtonLarge(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // زيادة الارتفاع ليتناسب مع الخط الكبير
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp) // أيقونة أكبر
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = contentColor,
                fontSize = 24.sp, // خط كبير وواضح جداً
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}
