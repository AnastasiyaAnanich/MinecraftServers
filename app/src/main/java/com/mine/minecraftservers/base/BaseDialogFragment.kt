package com.mine.minecraftservers.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.mine.minecraftservers.R

abstract class BaseDialogFragment<T : ViewBinding>(
    @StyleRes val defStyleRes: Int = STYLE_NO_FRAME
) : AppCompatDialogFragment() {

    private var _binding: T? = null

    protected val binding: T
        get() = _binding!!

    private var _navController: NavController? = null

    protected val navController: NavController
        get() = _navController!!

    private var requester: String = "${this::class.qualifiedName!!}#${hashCode()}"

    abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun getTheme() = defStyleRes

    abstract fun T.onBindView(saveInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requester = savedInstanceState?.getString(REQUESTER_KEY, requester) ?: requester
        _navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createViewBinding(inflater, container).run {
        _binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window!!.apply {
            WindowCompat.setDecorFitsSystemWindows(this, false)
            setBackgroundDrawableResource(R.color.grey)
        }
        binding.onBindView(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(REQUESTER_KEY, requester)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        _navController = null
    }

    fun setFragmentResultWithRequester(
        requester: String,
        vararg bundleInfo: Pair<String, Any?>
    ) {
        setFragmentResult(requester, bundleOf(REQUESTER_KEY to requester, *bundleInfo))
    }

    companion object {
        private const val REQUESTER_KEY = "REQUESTER_KEY"
    }
}