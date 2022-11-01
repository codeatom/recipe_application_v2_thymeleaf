package recipe_application.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import recipe_application.application.data.service.RecipeCategoryService;
import recipe_application.application.data.service.RecipeService;
import recipe_application.application.dto.forms.recipeCategoryForm.AddRecipeForm;
import recipe_application.application.dto.forms.recipeCategoryForm.CreateRecipeCategoryForm;
import recipe_application.application.dto.forms.recipeCategoryForm.UpdateRecipeCategoryForm;
import recipe_application.application.dto.views.RecipeCategoryView;
import recipe_application.application.dto.views.RecipeView;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/recipe-category")
public class RecipeCategoryController {

    private final RecipeCategoryService recipeCategoryService;
    private final RecipeService recipeService;

    @Autowired
    public RecipeCategoryController(RecipeCategoryService recipeCategoryService, RecipeService recipeService) {
        this.recipeCategoryService = recipeCategoryService;
        this.recipeService = recipeService;
    }

    @GetMapping("/list")
    public String getRecipeCategoryList(Model model) {
        model.addAttribute("recipeCategoryList", recipeCategoryService.findAll());

        return "recipe-category/recipe-categories";
    }

    @GetMapping("/get/{id}")
    public String getRecipeCategoryById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipeCategory", recipeCategoryService.findById(id));

        return "recipe-category/recipe-category";
    }

    @GetMapping("/add")
    public String addRecipeCategory(Model model) {
        CreateRecipeCategoryForm createRecipeCategoryForm = new CreateRecipeCategoryForm();
        model.addAttribute("recipeCategory", createRecipeCategoryForm);

        return "recipe-category/add-recipe-category";
    }

    @PostMapping("/add")
    public String addRecipeCategory(@Valid @ModelAttribute("recipeCategory") CreateRecipeCategoryForm createRecipeCategoryForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-category/add-recipe-category";
        }

        recipeCategoryService.save(createRecipeCategoryForm);

        return "redirect:/recipe-category/list";
    }

    @GetMapping("/update/{id}")
    public String updateRecipeCategory(@PathVariable("id") Integer id, Model model) {
        RecipeCategoryView recipeCategoryView = recipeCategoryService.findById(id);
        UpdateRecipeCategoryForm updateRecipeCategoryForm = new UpdateRecipeCategoryForm();

        updateRecipeCategoryForm.setId(id);
        updateRecipeCategoryForm.setCategory(recipeCategoryView.getCategory());

        model.addAttribute("recipeCategory", updateRecipeCategoryForm);

        return "recipe-category/update-recipe-category";
    }

    @PostMapping("/update")
    public String updateRecipeCategory(@ModelAttribute("recipeCategory") @Valid UpdateRecipeCategoryForm updateRecipeCategoryForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-category/update-recipe-category";
        }

        recipeCategoryService.update(updateRecipeCategoryForm);

        return "redirect:/recipe-category/list";
    }

    @GetMapping("/add-recipe/{id}")
    public String addRecipe(@PathVariable("id") Integer id, Model model) {
        List<RecipeView> recipeViews = (List<RecipeView>) recipeService.findAll();

        AddRecipeForm addRecipeForm = new AddRecipeForm();
        addRecipeForm.setId(id);

        model.addAttribute("recipeForm", addRecipeForm);
        model.addAttribute("recipeList", recipeViews);

        return "recipe-category/add-recipe-to-recipe-category";
    }

    @PostMapping("/add-recipe")
    public String addRecipe(@ModelAttribute("recipe") @Valid AddRecipeForm addRecipeForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-category/add-recipe-to-recipe-category";
        }

        recipeCategoryService.addRecipe(addRecipeForm);

        return "redirect:/recipe-category/get/" + addRecipeForm.getId();
    }

    @GetMapping("/remove-recipe/{recipeCategoryId}/{recipeId}")
    public String removeRecipe(@PathVariable("recipeCategoryId") Integer recipeCategoryId, @PathVariable("recipeId") Integer recipeId, RedirectAttributes ra) {
        recipeCategoryService.removeRecipe(recipeId, recipeCategoryId);

        return "redirect:/recipe-category/get/{recipeCategoryId}";
    }

    @GetMapping("/delete/request/{id}")
    public String deleteById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipeCategory", recipeCategoryService.findById(id));
        return "recipe-category/delete-request";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Integer id, RedirectAttributes ra) {
        recipeCategoryService.deleteById(id);
        return "redirect:/recipe-category/list";
    }

}
