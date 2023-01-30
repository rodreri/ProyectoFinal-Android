package mx.erick.araitastyles.ui.slideshow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import mx.erick.araitastyles.Login
import mx.erick.araitastyles.R
import mx.erick.araitastyles.TipoProvedor
import mx.erick.araitastyles.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var googleSignInOption: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val preferencias =
            activity?.getSharedPreferences(getString(R.string.file_preferencia), Context.MODE_PRIVATE)
        var email: String? = preferencias?.getString("email", null)
        var provedor: String? = preferencias?.getString("provedor", null)

        inicio(email ?: "", provedor ?: "")


        db.collection("users").document(email!!).get().addOnSuccessListener {
            binding.phone.setText(it.get("phone") as String?)
            binding.address.setText(it.get("address") as String?)
        }

        binding.saveButton.setOnClickListener{
            db.collection("users").document(email!!).set(
                hashMapOf("provider" to provedor,
                "address" to binding.address.text.toString(),
                "phone" to binding.phone.text.toString()
                )
            )

        }
        return root
    }

    private fun inicio(email: String, provedor: String) {
        binding.mail.text = email

        binding.closeSesion.setOnClickListener {
            val preferencias = activity?.getSharedPreferences(
                getString(R.string.file_preferencia),
                Context.MODE_PRIVATE
            )?.edit()
            preferencias?.clear()
            preferencias?.apply()
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(activity, Login::class.java))
            //finish()
        }
        //google
        if (provedor == TipoProvedor.GOOGLE.name) {
            googleSignInOption =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build()
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOption)
            val data = GoogleSignIn.getLastSignedInAccount(requireContext())
            if (data != null) {
                Picasso.get().load(data.photoUrl).into(binding.img)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}