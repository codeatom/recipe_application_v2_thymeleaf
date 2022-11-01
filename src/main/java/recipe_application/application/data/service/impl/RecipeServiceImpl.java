package recipe_application.application.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recipe_application.application.data.converter.Converter;
import recipe_application.application.data.repo.RecipeCategoryRepository;
import recipe_application.application.data.repo.RecipeIngredientRepository;
import recipe_application.application.data.repo.RecipeInstructionRepository;
import recipe_application.application.data.repo.RecipeRepository;
import recipe_application.application.data.service.RecipeService;
import recipe_application.application.dto.forms.recipeForm.AddRecipeCategoryForm;
import recipe_application.application.dto.forms.recipeForm.AddRecipeIngredientForm;
import recipe_application.application.dto.forms.recipeForm.CreateRecipeForm;
import recipe_application.application.dto.forms.recipeForm.UpdateRecipeForm;
import recipe_application.application.dto.views.RecipeIngredientView;
import recipe_application.application.dto.views.RecipeView;
import recipe_application.application.model.Recipe;
import recipe_application.application.model.RecipeCategory;
import recipe_application.application.model.RecipeIngredient;

import java.util.*;


@Transactional
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeCategoryRepository recipeCategoryRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeInstructionRepository recipeInstructionRepository;
    private final Converter converter;


    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             RecipeCategoryRepository recipeCategoryRepository,
                             RecipeIngredientRepository recipeIngredientRepository,
                             RecipeInstructionRepository recipeInstructionRepository,
                             Converter converter)
    {
        this.recipeRepository = recipeRepository;
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeInstructionRepository = recipeInstructionRepository;
        this.converter = converter;
    }

    @Override
    public RecipeView save(CreateRecipeForm createRecipeForm) {
        Recipe recipe = new Recipe(createRecipeForm.getRecipeName());
        recipe.setInstruction(recipeInstructionRepository.findById(createRecipeForm.getInstructionId()).get());
        Recipe savedRecipe = recipeRepository.save(recipe);

        return converter.recipeToView(savedRecipe);
    }

    @Override
    public RecipeView findById(Integer id) {
        return recipeRepository.findById(id).isPresent() ?
                converter.recipeToView(recipeRepository.findById(id).get()) :
                null;
    }

    @Override
    public Collection<RecipeView> findAll() {
        Collection<Recipe> recipeList = (Collection<Recipe>) recipeRepository.findAll();
        return converter.recipeListToViewList(recipeList);
    }

    public List<RecipeView> findAllByInstructionId(Integer id){
        List<Recipe> recipeList = recipeRepository.findAllByInstructionId(id);
        return new ArrayList<>(converter.recipeListToViewList(recipeList));
    }

    @Override
    public List<RecipeView> findAllByCategoriesCategory(String category) {
        List<Recipe> recipeList = recipeRepository.findAllByCategoriesCategory(category);
        return new ArrayList<>(converter.recipeListToViewList(recipeList));
    }

    @Override
    public List<RecipeView> findAllByRecipeNameContainingIgnoreCase(String recipeName){
        List<Recipe> recipeList = recipeRepository.findAllByRecipeNameContainingIgnoreCase(recipeName);
        return new ArrayList<>(converter.recipeListToViewList(recipeList));
    }

    @Override
    public List<RecipeView> findDistinctByCategoriesCategoryIn(Collection<String> categoryList) {
        List<Recipe> recipeList = recipeRepository.findDistinctByCategoriesCategoryIn(categoryList);
        return new ArrayList<>(converter.recipeListToViewList(recipeList));
    }

    @Override
    public List<RecipeView> findAllByRecipeIngredientsIngredientIngredientName(String ingredientName) {
        List<Recipe> recipeList = recipeRepository.findAllByRecipeIngredientsIngredientIngredientName(ingredientName);
        return new ArrayList<>(converter.recipeListToViewList(recipeList));
    }

    @Override
    public RecipeView update(UpdateRecipeForm updateRecipeForm) {
        Recipe recipe= recipeRepository.findById(updateRecipeForm.getId()).isPresent() ?
                recipeRepository.findById(updateRecipeForm.getId()).get() :
                null;

        if(recipe == null){
            return null;
        }

        recipe.setRecipeName(updateRecipeForm.getRecipeName());
        recipe.setInstruction(recipeInstructionRepository.findById(updateRecipeForm.getInstructionId()).get());

        return converter.recipeToView(recipe);
    }

    @Override
    public boolean deleteById(Integer id) {
        if(recipeRepository.existsById(id)){
            removeAssociatedEntity(id);
            recipeRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Recipe recipe) {
        if(recipeRepository.existsById(recipe.getId())){
            removeAssociatedEntity(recipe.getId());
            recipeRepository.delete(recipe);
            return true;
        }

        return false;
    }

    @Override
    public RecipeView addRecipeIngredient(AddRecipeIngredientForm addRecipeIngredientForm){
        Recipe recipe = recipeRepository.findById(addRecipeIngredientForm.getId()).isPresent() ?
                recipeRepository.findById(addRecipeIngredientForm.getId()).get() :
                null;

        if(recipe == null){
            return null;
        }

        if(recipeIngredientRepository.findById(addRecipeIngredientForm.getRecipeIngredientId()).isPresent()){
            recipe.addIngredient(recipeIngredientRepository.findById(addRecipeIngredientForm.getRecipeIngredientId()).get());
        }

        return converter.recipeToView(recipe);
    }

    @Override
    public void removeRecipeIngredient(Integer recipeId, Integer recipeIngredientId){
        Recipe recipe = recipeRepository.findById(recipeId).isPresent() ?
                recipeRepository.findById(recipeId).get() :
                null;

        RecipeIngredient recipeIngredient = recipeIngredientRepository.findById(recipeIngredientId).isPresent() ?
                recipeIngredientRepository.findById(recipeIngredientId).get() :
                null;

        if(recipe != null && recipeIngredient != null){
            recipe.removeIngredient(recipeIngredient);
        }
    }

    @Override
    public RecipeView addRecipeCategory(AddRecipeCategoryForm addRecipeCategoryForm){
        Recipe recipe = recipeRepository.findById(addRecipeCategoryForm.getId()).isPresent() ?
                recipeRepository.findById(addRecipeCategoryForm.getId()).get() :
                null;

        if(recipe == null){
            return null;
        }

        if(recipeCategoryRepository.findById(addRecipeCategoryForm.getRecipeCategoryId()).isPresent()){
            recipe.addCategory(recipeCategoryRepository.findById(addRecipeCategoryForm.getRecipeCategoryId()).get());
        }

        return converter.recipeToView(recipe);
    }

    @Override
    public void removeRecipeCategory(Integer recipeId, Integer recipeCategoryId){
        Recipe recipe = recipeRepository.findById(recipeId).isPresent() ?
                recipeRepository.findById(recipeId).get() :
                null;

        RecipeCategory recipeCategory = recipeCategoryRepository.findById(recipeCategoryId).isPresent() ?
                recipeCategoryRepository.findById(recipeCategoryId).get() :
                null;

        if(recipe != null && recipeCategory != null){
            recipe.removeRecipeCategory(recipeCategory);
        }
    }

    private void removeAssociatedEntity(Integer id){
        Recipe recipe = recipeRepository.findById(id).get();
        List<RecipeCategory> recipeCategoryList = (List<RecipeCategory>) recipeCategoryRepository.findAll();
        List<RecipeIngredient> recipeIngredientList = (List<RecipeIngredient>) recipeIngredientRepository.findAll();

        recipeCategoryList.forEach(recipeCategory ->  recipeCategory.getRecipes().remove(recipe));

        recipeIngredientList.forEach(recipeIngredient -> recipeIngredient.setRecipe(null));

        // recipe.setInstruction(null);
    }

}
