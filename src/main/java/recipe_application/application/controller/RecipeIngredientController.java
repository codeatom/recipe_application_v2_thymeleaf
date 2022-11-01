package recipe_application.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import recipe_application.application.data.service.IngredientService;
import recipe_application.application.data.service.RecipeIngredientService;
import recipe_application.application.data.service.RecipeService;
import recipe_application.application.dto.forms.recipeIngredientForm.AddRecipeForm;
import recipe_application.application.dto.forms.recipeIngredientForm.CreateRecipeIngredientForm;
import recipe_application.application.dto.forms.recipeIngredientForm.UpdateRecipeIngredientForm;
import recipe_application.application.dto.views.IngredientView;
import recipe_application.application.dto.views.RecipeIngredientView;
import recipe_application.application.dto.views.RecipeView;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/recipe-ingredient")
public class RecipeIngredientController {

    private final RecipeIngredientService recipeIngredientService;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    @Autowired
    public RecipeIngredientController(RecipeIngredientService recipeIngredientService, IngredientService ingredientService, RecipeService recipeService) {
        this.recipeIngredientService = recipeIngredientService;
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
    }

    @GetMapping("/list")
    public String getRecipeIngredientList(Model model) {
        model.addAttribute("recipeIngredientList", recipeIngredientService.findAll());

        return "recipe-ingredient/recipe-ingredients";
    }

    @GetMapping("/get/{id}")
    public String getRecipeIngredientById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipeIngredient", recipeIngredientService.findById(id));

        return "recipe-ingredient/recipe-ingredient";
    }

    @GetMapping("/get-by-ingredient/{ingredientId}")
    public String getRecipeIngredientByIngredientId(@PathVariable("ingredientId") Integer ingredientId, Model model) {
        model.addAttribute("recipeIngredient", recipeIngredientService.findAllByIngredientId(ingredientId));

        return "recipe-ingredient/recipe-ingredient";
    }

    @GetMapping("/add")
    public String addRecipeIngredient(Model model) {
        CreateRecipeIngredientForm createRecipeIngredientForm = new CreateRecipeIngredientForm();
        List<IngredientView> ingredientViewList = (List<IngredientView>) ingredientService.findAll();

        model.addAttribute("recipeIngredient", createRecipeIngredientForm);
        model.addAttribute("ingredientList", ingredientViewList);

        return "recipe-ingredient/add-recipe-ingredient";
    }

    @PostMapping("/add")
    public String addRecipeIngredient(@Valid @ModelAttribute("recipeIngredient") CreateRecipeIngredientForm createRecipeIngredientForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-ingredient/add-recipe-ingredient";
        }

        recipeIngredientService.save(createRecipeIngredientForm);

        return "redirect:/recipe-ingredient/list";
    }

    @GetMapping("/update/{id}")
    public String updateRecipeIngredient(@PathVariable("id") Integer id, Model model) {
        RecipeIngredientView recipeIngredientView = recipeIngredientService.findById(id);
        List<IngredientView> ingredientViewList = (List<IngredientView>) ingredientService.findAll();

        UpdateRecipeIngredientForm updateRecipeIngredientForm = new UpdateRecipeIngredientForm();

        updateRecipeIngredientForm.setId(id);
        updateRecipeIngredientForm.setAmount(recipeIngredientView.getAmount());
        updateRecipeIngredientForm.setMeasurement(recipeIngredientView.getMeasurement());

        model.addAttribute("recipeIngredient", updateRecipeIngredientForm);
        model.addAttribute("ingredientList", ingredientViewList);

        return "recipe-ingredient/update-recipe-ingredient";
    }

    @PostMapping("/update")
    public String updateRecipeIngredient(@ModelAttribute("recipeIngredient") @Valid UpdateRecipeIngredientForm updateRecipeIngredientForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-ingredient/update-recipe-ingredient";
        }

        recipeIngredientService.update(updateRecipeIngredientForm);

        return "redirect:/recipe-ingredient/list";
    }

    @GetMapping("/add-recipe/{id}")
    public String addRecipeToRecipeIngredient(@PathVariable("id") Integer id, Model model) {
        List<RecipeView> recipeViewList = (List<RecipeView>) recipeService.findAll();

        AddRecipeForm addRecipeForm = new AddRecipeForm();
        addRecipeForm.setId(id);

        model.addAttribute("recipeForm", addRecipeForm);
        model.addAttribute("recipeList", recipeViewList);

        return "recipe-ingredient/add-recipe-to-recipe-ingredient";
    }

    @PostMapping("/add-recipe")
    public String addRecipeToRecipeIngredient(@ModelAttribute("recipeIngredient") @Valid AddRecipeForm addRecipeForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-ingredient/add-recipe-to-recipe-ingredient";
        }

        recipeIngredientService.addRecipe(addRecipeForm);

        return "redirect:/recipe-ingredient/get/" + addRecipeForm.getId();
    }

    @GetMapping("/remove-recipe/{id}")
    public String removeRecipeFromRecipeIngredient(@PathVariable("id") Integer id, RedirectAttributes ra) {
        recipeIngredientService.removeRecipe(id);

        return "redirect:/recipe-ingredient/get/{id}";
    }

    @GetMapping("/delete/request/{id}")
    public String deleteById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipeIngredient", recipeIngredientService.findById(id));
        return "recipe-ingredient/delete-request";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Integer id, RedirectAttributes ra) {
        recipeIngredientService.deleteById(id);
        return "redirect:/recipe-ingredient/list";
    }

}
