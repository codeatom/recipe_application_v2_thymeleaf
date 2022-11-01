package recipe_application.application.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recipe_application.application.data.converter.Converter;
import recipe_application.application.data.repo.IngredientRepository;
import recipe_application.application.data.repo.RecipeIngredientRepository;
import recipe_application.application.data.service.IngredientService;
import recipe_application.application.dto.forms.ingredientForm.CreateIngredientForm;
import recipe_application.application.dto.forms.ingredientForm.UpdateIngredientForm;
import recipe_application.application.dto.views.IngredientView;
import recipe_application.application.model.Ingredient;
import recipe_application.application.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Transactional
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final Converter converter;

    @Autowired
    public IngredientServiceImpl(IngredientRepository ingredientRepository, RecipeIngredientRepository recipeIngredientRepository, Converter converter) {
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.converter = converter;
    }

    @Override
    public IngredientView save(CreateIngredientForm createIngredientForm) {
        Ingredient ingredient = ingredientRepository.save(new Ingredient(createIngredientForm.getIngredientName()));
        return converter.ingredientToView(ingredient);
    }

    @Override
    public IngredientView findById(Integer id) {
        return ingredientRepository.findById(id).isPresent() ?
                converter.ingredientToView(ingredientRepository.findById(id).get()) :
                null;
    }

    @Override
    public Collection<IngredientView> findAll() {
        Collection<Ingredient> ingredientList = (Collection<Ingredient>) ingredientRepository.findAll();
        return converter.ingredientListToViewList(ingredientList);
    }

    @Override
    public IngredientView findByIngredientNameIgnoreCase(String ingredientName) {
        return ingredientRepository.findByIngredientNameIgnoreCase(ingredientName).isPresent() ?
                converter.ingredientToView(ingredientRepository.findByIngredientNameIgnoreCase(ingredientName).get()) :
                null;
    }

    @Override
    public List<IngredientView> findByIngredientNameContainsIgnoreCase(String ingredientName) {
        Collection<Ingredient> ingredientList = ingredientRepository.findByIngredientNameContainsIgnoreCase(ingredientName);
        Collection<IngredientView> ingredientViews = converter.ingredientListToViewList(ingredientList);

        return new ArrayList<>(ingredientViews);
    }

    @Override
    public IngredientView update(UpdateIngredientForm updateIngredientForm) {
        Ingredient ingredient = ingredientRepository.findById(updateIngredientForm.getId()).isPresent() ?
                ingredientRepository.findById(updateIngredientForm.getId()).get() :
                null;

        if(ingredient == null){
            return null;
        }

        ingredient.setIngredientName(updateIngredientForm.getIngredientName());

        return converter.ingredientToView(ingredient);
    }

    @Override
    public boolean deleteById(Integer id) {
        if(ingredientRepository.existsById(id)){
            removeAssociatedEntity(id);
            ingredientRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Ingredient ingredient) {
        if(ingredientRepository.existsById(ingredient.getId())){
            removeAssociatedEntity(ingredient.getId());
            ingredientRepository.delete(ingredient);
            return true;
        }

        return false;
    }

    private void removeAssociatedEntity(Integer id){
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAllByIngredientId(id);
        recipeIngredientList.forEach(recipeIngredient -> recipeIngredient.setIngredient(null));
    }

}
