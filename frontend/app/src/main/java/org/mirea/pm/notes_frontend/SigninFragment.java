package org.mirea.pm.notes_frontend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mirea.pm.notes_frontend.databinding.FragmentSigninBinding;


public class SigninFragment extends Fragment {

    private org.mirea.pm.notes_frontend.databinding.FragmentSigninBinding binding;

    // TODO: Rename and change types and number of parameters
    public static SigninFragment newInstance(String param1, String param2) {
        return new SigninFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentSigninBinding.inflate(inflater);

        binding.signupButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.signupFragment);
        });

        return binding.getRoot();
    }
}