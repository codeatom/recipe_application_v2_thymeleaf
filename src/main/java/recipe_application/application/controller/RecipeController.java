package recipe_application.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import recipe_application.application.data.service.RecipeCategoryService;
import recipe_application.application.data.service.RecipeIngredientService;
import recipe_application.application.data.service.RecipeInstructionService;
import recipe_application.application.data.service.RecipeService;
import recipe_application.application.dto.forms.recipeForm.AddRecipeCategoryForm;
import recipe_application.application.dto.forms.recipeForm.AddRecipeIngredientForm;
import recipe_application.application.dto.forms.recipeForm.CreateRecipeForm;
import recipe_application.application.dto.forms.recipeForm.UpdateRecipeForm;
import recipe_application.application.dto.forms.recipeInstructionForm.CreateRecipeInstructionForm;
import recipe_application.application.dto.forms.recipeInstructionForm.UpdateRecipeInstructionForm;
import recipe_application.application.dto.views.RecipeCategoryView;
import recipe_application.application.dto.views.RecipeIngredientView;
import recipe_application.application.dto.views.RecipeInstructionView;
import recipe_application.application.dto.views.RecipeView;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeInstructionService recipeInstructionService;
    private final RecipeIngredientService recipeIngredientService;
    private final RecipeCategoryService recipeCategoryService;

    @Autowired
    public RecipeController(RecipeService recipeService,
                            RecipeInstructionService recipeInstructionService,
                            RecipeIngredientService recipeIngredientService,
                            RecipeCategoryService recipeCategoryService
    )
    {
        this.recipeService = recipeService;
        this.recipeInstructionService = recipeInstructionService;
        this.recipeIngredientService = recipeIngredientService;
        this.recipeCategoryService = recipeCategoryService;
    }

    @GetMapping("/list")
    public String getRecipeList(Model model) {
        model.addAttribute("recipeList", recipeService.findAll());

        return "recipe/recipes";
    }

    @GetMapping("/get/{id}")
    public String getRecipeById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipe", recipeService.findById(id));

        return "recipe/recipe";
    }

    @GetMapping("/search")
    public String getRecipeByRecipeName(@RequestParam("search") String recipeName, Model model) {
        model.addAttribute("recipeList", recipeService.findAllByRecipeNameContainingIgnoreCase(recipeName));

        return "recipe/recipes";
    }

    @GetMapping("/add")
    public String addRecipe(Model model) {
        CreateRecipeForm createRecipeForm = new CreateRecipeForm();

        model.addAttribute("recipe", createRecipeForm);

        return "recipe/add-recipe";
    }

    @PostMapping("/add")
    public String addRecipe(@Valid @ModelAttribute("recipe") CreateRecipeForm createRecipeForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe/add-recipe";
        }

        CreateRecipeInstructionForm createRecipeInstructionForm = new CreateRecipeInstructionForm();
        createRecipeInstructionForm.setTitle(createRecipeForm.getInstructionTitle());
        createRecipeInstructionForm.setInstruction(createRecipeForm.getInstructionDetail());
        RecipeInstructionView recipeInstructionView = recipeInstructionService.save(createRecipeInstructionForm);

        createRecipeForm.setInstructionId(recipeInstructionView.getId());
        recipeService.save(createRecipeForm);

        return "redirect:/recipe/list";
    }

    @GetMapping("/update/{id}")
    public String updateRecipe(@PathVariable("id") Integer id, Model model) {
        RecipeView recipeView = recipeService.findById(id);

        UpdateRecipeForm updateRecipeForm = new UpdateRecipeForm();

        updateRecipeForm.setId(id);
        updateRecipeForm.setRecipeName(recipeView.getRecipeName());

        updateRecipeForm.setInstructionId(recipeView.getInstruction().getId());
        updateRecipeForm.setInstructionTitle(recipeView.getInstruction().getTitle());
        updateRecipeForm.setInstructionDetail(recipeView.getInstruction().getInstruction());

        model.addAttribute("recipe", updateRecipeForm);

        return "recipe/update-recipe";
    }

    @PostMapping("/update")
    public String updateRecipe(@ModelAttribute("recipe") @Valid UpdateRecipeForm updateRecipeForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe/update-recipe";
        }

        UpdateRecipeInstructionForm updateRecipeInstructionForm = new UpdateRecipeInstructionForm();

        updateRecipeInstructionForm.setId(updateRecipeForm.getInstructionId());
        updateRecipeInstructionForm.setTitle(updateRecipeForm.getInstructionTitle());
        updateRecipeInstructionForm.setInstruction(updateRecipeForm.getInstructionDetail());
        recipeInstructionService.update(updateRecipeInstructionForm);

        recipeService.update(updateRecipeForm);

        return "redirect:/recipe/list";
    }

    @GetMapping("/add-recipe-ingredient/{id}")
    public String addRecipeIngredientToRecipe(@PathVariable("id") Integer id, Model model) {
        List<RecipeIngredientView> recipeIngredientViews = (List<RecipeIngredientView>) recipeIngredientService.findAll();

        AddRecipeIngredientForm addRecipeIngredientForm = new AddRecipeIngredientForm();
        addRecipeIngredientForm.setId(id);

        model.addAttribute("recipeIngredientForm", addRecipeIngredientForm);
        model.addAttribute("recipeIngredientList", recipeIngredientViews);

        return "recipe/add-recipe-ingredient-to-recipe";
    }

    @PostMapping("/add-recipe-ingredient")
    public String addRecipeIngredient(@ModelAttribute("recipeIngredient") @Valid AddRecipeIngredientForm addRecipeIngredientForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe/add-recipe-ingredient-to-recipe";
        }

        recipeService.addRecipeIngredient(addRecipeIngredientForm);

        return "redirect:/recipe/get/" + addRecipeIngredientForm.getId();
    }

    @GetMapping("/remove-recipe-ingredient/{recipeId}/{recipeIngredientId}")
    public String removeRecipeIngredient(@PathVariable("recipeId") Integer recipeId, @PathVariable("recipeIngredientId") Integer recipeIngredientId, RedirectAttributes ra) {
        recipeService.removeRecipeIngredient(recipeId, recipeIngredientId);

        return "redirect:/recipe/get/{recipeId}";
    }

    @GetMapping("/add-recipe-category/{id}")
    public String addRecipeCategoryToRecipe(@PathVariable("id") Integer id, Model model) {
        List<RecipeCategoryView> recipeCategoryViews = (List<RecipeCategoryView>) recipeCategoryService.findAll();

        AddRecipeCategoryForm addRecipeCategoryForm = new AddRecipeCategoryForm();
        addRecipeCategoryForm.setId(id);

        model.addAttribute("recipeCategoryForm", addRecipeCategoryForm);
        model.addAttribute("recipeCategoryList", recipeCategoryViews);

        return "recipe/add-recipe-category-to-recipe";
    }

    @PostMapping("/add-recipe-category")
    public String addRecipeCategory(@ModelAttribute("recipeCategory") @Valid AddRecipeCategoryForm addRecipeCategoryForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe/add-recipe-category-to-recipe";
        }

        recipeService.addRecipeCategory(addRecipeCategoryForm);

        return "redirect:/recipe/get/" + addRecipeCategoryForm.getId();
    }

    @GetMapping("/remove-recipe-category/{recipeId}/{recipeCategoryId}")
    public String removeRecipeCategory(@PathVariable("recipeId") Integer recipeId, @PathVariable("recipeCategoryId") Integer recipeCategoryId, RedirectAttributes ra) {
        recipeService.removeRecipeCategory(recipeId, recipeCategoryId);

        return "redirect:/recipe/get/{recipeId}";
    }

    @GetMapping("/delete/request/{id}")
    public String deleteById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipe", recipeService.findById(id));
        return "recipe/delete-request";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Integer id, RedirectAttributes ra) {
        recipeService.deleteById(id);
        return "redirect:/recipe/list";
    }
}
