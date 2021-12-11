package br.com.appestetica.ui.clients;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.appestetica.ui.clients.model.Client;

public class ClientsViewModel extends ViewModel {

    private MutableLiveData<List<Client>> mClients;

    public ClientsViewModel() {
        mClients = new MutableLiveData<>();
    }

    public LiveData<List<Client>> getClients() {
        if (mClients == null){
            mClients = new MutableLiveData<>();
            loadClients();
        }
            return mClients;
    }

    private void loadClients() {

    }
}