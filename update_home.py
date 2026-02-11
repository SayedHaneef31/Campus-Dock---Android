#!/usr/bin/env python3
# Script to modernize home_fragment.xml

import re

file_path = r'd:\Android Projects 2025\Campus Dock\app\src\main\res\layout\home_fragment.xml'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Define old section (Pav Bhaji section)
old_section = '''            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:weightSum="2"
                android:layout_marginTop="12dp">

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:orientation="vertical"
                    android:padding="8dp"
                    android:background="@drawable/rounded_card">

                    <ImageView
                        android:layout_width="match_parent"
                        android:layout_height="80dp"
                        android:scaleType="centerCrop"
                        android:src="@drawable/pav"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Pav Bhaji"
                        android:textStyle="bold"
                        android:textSize="14sp"
                        android:paddingTop="4dp"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Tea man's café"
                        android:textSize="12sp"/>
                </LinearLayout>

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1"
                    android:orientation="vertical"
                    android:padding="8dp"
                    android:background="@drawable/rounded_card"
                    android:layout_marginStart="8dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="/Senior Advice"
                        android:textStyle="bold"
                        android:layout_marginTop="8dp"
                        android:textSize="20sp"/>

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="8sp"
                        android:text="For third year focus more on the development side because for the internship you will be asked..."
                        android:textSize="12sp"
                        android:maxLines="4"
                        android:ellipsize="end"/>
                </LinearLayout>
            </LinearLayout>

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Top Deals in Campus: 💯"
                android:textStyle="bold"
                android:textSize="20sp"
                android:layout_marginTop="16dp"/>'''

new_section = '''            <!-- Pav Bhaji and Senior Advice Cards - Modern Design -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_marginTop="20dp">

                <!-- Pav Bhaji Card -->
                <com.google.android.material.card.MaterialCardView
                    android:layout_width="0dp"
                    android:layout_height="180dp"
                    android:layout_weight="1"
                    android:layout_marginEnd="8dp"
                    app:cardCornerRadius="16dp"
                    app:cardElevation="2dp"
                    app:strokeWidth="0dp"
                    android:foreground="?android:attr/selectableItemBackground">

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:orientation="vertical">

                        <ImageView
                            android:layout_width="match_parent"
                            android:layout_height="0dp"
                            android:layout_weight="1"
                            android:scaleType="centerCrop"
                            android:src="@drawable/pav"
                            android:contentDescription="Pav Bhaji"/>

                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:orientation="vertical"
                            android:padding="12dp"
                            android:background="?android:colorBackground">

                            <TextView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="Pav Bhaji"
                                android:textStyle="bold"
                                android:textSize="15sp"
                                android:textColor="?attr/colorOnSurface"
                                android:letterSpacing="0.01"/>

                            <TextView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="Tea man's café"
                                android:textSize="12sp"
                                android:textColor="?attr/colorOnSurfaceVariant"
                                android:layout_marginTop="3dp"/>
                        </LinearLayout>
                    </LinearLayout>
                </com.google.android.material.card.MaterialCardView>

                <!-- Senior Advice Card -->
                <com.google.android.material.card.MaterialCardView
                    android:layout_width="0dp"
                    android:layout_height="180dp"
                    android:layout_weight="1"
                    android:layout_marginStart="8dp"
                    app:cardCornerRadius="16dp"
                    app:cardElevation="2dp"
                    app:strokeWidth="0dp"
                    android:foreground="?android:attr/selectableItemBackground"
                    android:background="@color/colorPrimary">

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:orientation="vertical"
                        android:padding="14dp"
                        android:gravity="top"
                        android:background="@color/colorPrimary">

                        <LinearLayout
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:gravity="center_vertical"
                            android:layout_marginBottom="10dp">

                            <ImageView
                                android:layout_width="18dp"
                                android:layout_height="18dp"
                                android:src="@drawable/ic_wisdom"
                                android:scaleType="fitCenter"
                                android:tint="@android:color/white"
                                android:contentDescription="Wisdom"/>

                            <TextView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="Senior Advice"
                                android:textStyle="bold"
                                android:textSize="14sp"
                                android:textColor="@android:color/white"
                                android:layout_marginStart="6dp"
                                android:letterSpacing="0.01"/>
                        </LinearLayout>

                        <TextView
                            android:layout_width="match_parent"
                            android:layout_height="0dp"
                            android:layout_weight="1"
                            android:text="Focus on development in 3rd year. Internship interviews prioritize practical skills over theory..."
                            android:textSize="12sp"
                            android:textColor="@android:color/white"
                            android:maxLines="5"
                            android:ellipsize="end"
                            android:lineSpacingMultiplier="1.35"/>

                        <LinearLayout
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:gravity="center_vertical"
                            android:layout_marginTop="8dp">

                            <TextView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="Learn more"
                                android:textSize="11sp"
                                android:textColor="@android:color/white"
                                android:textStyle="bold"
                                android:letterSpacing="0.01"/>

                            <ImageView
                                android:layout_width="14dp"
                                android:layout_height="14dp"
                                android:src="@drawable/ic_arrow_forward"
                                android:scaleType="fitCenter"
                                android:tint="@android:color/white"
                                android:layout_marginStart="4dp"
                                android:contentDescription="Arrow"/>
                        </LinearLayout>
                    </LinearLayout>
                </com.google.android.material.card.MaterialCardView>
            </LinearLayout>

            <!-- Top Deals Header with See All Link -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical"
                android:layout_marginTop="24dp"
                android:layout_marginBottom="2dp">

                <TextView
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:text="Top Deals in Campus 💯"
                    android:textStyle="bold"
                    android:textSize="20sp"
                    android:textColor="?attr/colorOnBackground"
                    android:letterSpacing="0.01"/>

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="See all"
                    android:textSize="12sp"
                    android:textStyle="bold"
                    android:textColor="@color/colorPrimary"
                    android:layout_marginEnd="4dp"
                    android:letterSpacing="0.01"/>
            </LinearLayout>'''

content = content.replace(old_section, new_section)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Successfully updated home_fragment.xml!")
