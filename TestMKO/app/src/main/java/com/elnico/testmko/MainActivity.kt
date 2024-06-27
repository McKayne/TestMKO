package com.elnico.testmko

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import com.elnico.testmko.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            if (!isAccessibilityServiceEnabled()) {
                openAccessibilitySettings()
                //presentMessage("sgssrrh")
            } else {
                if (!hasUsageStatsPermission(this)) {
                    requestUsageStatsPermission()
                } else {
                    openInstagramApp()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.nameFlow.filterNotNull().collect {
                binding.previousResultTextView.text = it
            }
        }

        viewModel.fetchPreviousName()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPreviousName()

        binding.resultTextView.text = StringValueHolder.loggedInUserName ?: "--/--"
    }

    private fun requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(this)) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps =
            context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            "android:get_usage_stats",
            Process.myUid(), context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun openInstagramApp() {
        val appUri = Uri.parse("instagram://profile")

        try { //first try to open in instagram app
            val  appIntent = packageManager.getLaunchIntentForPackage("com.instagram.android")
            if (appIntent != null) {
                appIntent.setAction(Intent.ACTION_VIEW)
                appIntent.setData(appUri)
                startActivity(appIntent)
            } else {
                presentMessage("Приложение Instagram не установлено")
            }
        } catch(e: Exception){ //or else open in browser
            presentMessage("Приложение Instagram не установлено")
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = service.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        for (enabledService in enabledServices) {
            val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName.equals(packageName)) // && enabledServiceInfo.name.equals(InterceptorService::class.java))
                return true
        }

        return false
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun presentMessage(message: String) {
        val rootView = window.decorView.rootView
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}