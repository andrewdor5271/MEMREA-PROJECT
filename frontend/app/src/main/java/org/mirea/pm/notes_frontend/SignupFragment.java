package org.mirea.pm.notes_frontend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mirea.pm.notes_frontend.databinding.FragmentSigninBinding;
import org.mirea.pm.notes_frontend.databinding.FragmentSignupBinding;


public class SignupFragment extends Fragment {

    private org.mirea.pm.notes_frontend.databinding.FragmentSignupBinding binding;

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance(String param1, String param2) {
        return new SignupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater);

        binding.signinButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.signinFragment);
        });

        return binding.getRoot();
    }
}