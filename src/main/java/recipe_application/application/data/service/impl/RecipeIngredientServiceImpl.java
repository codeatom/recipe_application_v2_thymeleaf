package recipe_application.application.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recipe_application.application.data.converter.Converter;
import recipe_application.application.data.repo.IngredientRepository;
import recipe_application.application.data.repo.RecipeIngredientRepository;
import recipe_application.application.data.repo.RecipeRepository;
import recipe_application.application.data.service.RecipeIngredientService;
import recipe_application.application.dto.forms.recipeIngredientForm.AddRecipeForm;
import recipe_application.application.dto.forms.recipeIngredientForm.CreateRecipeIngredientForm;
import recipe_application.application.dto.forms.recipeIngredientForm.UpdateRecipeIngredientForm;
import recipe_application.application.dto.views.RecipeIngredientView;
import recipe_application.application.model.Ingredient;
import recipe_application.application.model.Recipe;
import recipe_application.application.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Transactional
@Service
public class RecipeIngredientServiceImpl implements RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final Converter converter;

    @Autowired
    public RecipeIngredientServiceImpl(RecipeIngredientRepository recipeIngredientRepository, IngredientRepository ingredientRepository, RecipeRepository recipeRepository, Converter converter) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.converter = converter;
    }

    @Override
    public RecipeIngredientView save(CreateRecipeIngredientForm recipeIngredientForm) {
        Ingredient ingredient = ingredientRepository.findById(recipeIngredientForm.getIngredientId()).get();

        RecipeIngredient recipeIngredient = new RecipeIngredient(
                recipeIngredientForm.getAmount(),
                recipeIngredientForm.getMeasurement(),
                ingredient);

        RecipeIngredient savedRecipeIngredient = recipeIngredientRepository.save(recipeIngredient);

        return converter.recipeIngredientToView(savedRecipeIngredient);
    }

    @Override
    public RecipeIngredientView findById(Integer id) {
        return recipeIngredientRepository.findById(id).isPresent() ?
                converter.recipeIngredientToView(recipeIngredientRepository.findById(id).get()) :
                null;
    }

    @Override
    public Collection<RecipeIngredientView> findAll() {
        Collection<RecipeIngredient> recipeIngredientList = (Collection<RecipeIngredient>) recipeIngredientRepository.findAll();
        return converter.recipeIngredientListToViewList(recipeIngredientList);
    }

    public List<RecipeIngredientView> findAllByIngredientId(Integer id) {
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAllByIngredientId(id);
        return new ArrayList<>(converter.recipeIngredientListToViewList(recipeIngredientList));
    }

    @Override
    public RecipeIngredientView update(UpdateRecipeIngredientForm updateRecipeIngredientForm) {
        RecipeIngredient recipeIngredient = recipeIngredientRepository.findById(updateRecipeIngredientForm.getId()).isPresent() ?
                recipeIngredientRepository.findById(updateRecipeIngredientForm.getId()).get() :
                null;

        if(recipeIngredient == null){
            return null;
        }

        recipeIngredient.setAmount(updateRecipeIngredientForm.getAmount());
        recipeIngredient.setMeasurement(updateRecipeIngredientForm.getMeasurement());

        if(ingredientRepository.findById(updateRecipeIngredientForm.getIngredientId()).isPresent()){
            recipeIngredient.setIngredient(ingredientRepository.findById(updateRecipeIngredientForm.getIngredientId()).get());
        }

        return converter.recipeIngredientToView(recipeIngredient);
    }

    @Override
    public boolean deleteById(Integer id) {
        if(recipeIngredientRepository.existsById(id)){
            removeAssociatedEntity(id);
            recipeIngredientRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(RecipeIngredient recipeIngredient) {
        if(recipeIngredientRepository.existsById(recipeIngredient.getId())){
            removeAssociatedEntity(recipeIngredient.getId());
            recipeIngredientRepository.delete(recipeIngredient);
            return true;
        }

        return false;
    }

    private void removeAssociatedEntity(Integer id){
        RecipeIngredient recipeIngredient = recipeIngredientRepository.findById(id).get();

        recipeIngredient.setIngredient(null);
        recipeIngredient.setRecipe(null);
    }

    @Override
    public RecipeIngredientView addRecipe(AddRecipeForm addRecipeForm){
        RecipeIngredient recipeIngredient = recipeIngredientRepository.findById(addRecipeForm.getId()).isPresent() ?
                recipeIngredientRepository.findById(addRecipeForm.getId()).get() :
                null;

        if(recipeIngredient == null){
            return null;
        }

        if(recipeRepository.findById(addRecipeForm.getRecipeId()).isPresent()){
            recipeIngredient.addRecipe(recipeRepository.findById(addRecipeForm.getRecipeId()).get());
        }

        return converter.recipeIngredientToView(recipeIngredient);
    }

    public void removeRecipe(Integer id){
        if(recipeIngredientRepository.findById(id).isPresent()){
            RecipeIngredient recipeIngredient = recipeIngredientRepository.findById(id).get();

            recipeIngredient.removeRecipe(recipeIngredient.getRecipe());
        }
    }

}
