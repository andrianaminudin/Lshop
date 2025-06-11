package com.example.lshop.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.lshop.Domain.BannerModel;
import com.example.lshop.Domain.CategoryModel;
import com.example.lshop.Domain.ItemModel;
import com.example.lshop.Respository.MainRespository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private final MainRespository respository=new MainRespository();

    public LiveData<ArrayList<CategoryModel>> loadCategory(){
        return respository.loadCategory();
    }

    public LiveData<ArrayList<BannerModel>> loadBanner() {
        return respository.loadBanner();
    }

    public LiveData<ArrayList<ItemModel>> loadPopular() {
        return respository.loadPopular();
    }

}
