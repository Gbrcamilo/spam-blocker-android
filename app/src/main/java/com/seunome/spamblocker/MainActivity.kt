package com.seunome.spamblocker

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seunome.spamblocker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsRepository = SettingsRepository(this)

        setupUi()
        refreshStatus()
    }

    private fun setupUi() {
        binding.switchBlockUnknown.isChecked = settingsRepository.isBlockingEnabled()
        binding.switchSkipCallLog.isChecked = settingsRepository.shouldSkipCallLog()
        binding.switchSkipNotification.isChecked = settingsRepository.shouldSkipNotification()

        binding.switchBlockUnknown.setOnCheckedChangeListener { _, isChecked ->
            settingsRepository.setBlockingEnabled(isChecked)
        }

        binding.switchSkipCallLog.setOnCheckedChangeListener { _, isChecked ->
            settingsRepository.setSkipCallLog(isChecked)
        }

        binding.switchSkipNotification.setOnCheckedChangeListener { _, isChecked ->
            settingsRepository.setSkipNotification(isChecked)
        }

        binding.buttonRequestRole.setOnClickListener {
            requestCallScreeningRole()
        }

        binding.buttonOpenRoleSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        val granted = isCallScreeningRoleHeld()
        binding.textRoleStatus.text = if (granted) {
            getString(R.string.role_status_active)
        } else {
            getString(R.string.role_status_inactive)
        }
    }

    private fun isCallScreeningRoleHeld(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val roleManager = getSystemService(RoleManager::class.java) ?: return false
        return roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }

    private fun requestCallScreeningRole() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(this, "Este app exige Android 10 ou superior.", Toast.LENGTH_LONG).show()
            return
        }

        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager == null) {
            Toast.makeText(this, "RoleManager indisponivel neste aparelho.", Toast.LENGTH_LONG).show()
            return
        }

        if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            Toast.makeText(this, "Seu app ja e o filtro de chamadas ativo.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        startActivityForResult(intent, REQUEST_CALL_SCREENING_ROLE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CALL_SCREENING_ROLE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Permissao de filtro de chamadas concedida.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "O papel de filtro de chamadas nao foi concedido.", Toast.LENGTH_LONG).show()
            }
            refreshStatus()
        }
    }

    companion object {
        private const val REQUEST_CALL_SCREENING_ROLE = 1001
    }
}
