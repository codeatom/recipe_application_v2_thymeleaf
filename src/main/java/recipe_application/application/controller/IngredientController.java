package recipe_application.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import recipe_application.application.data.service.IngredientService;
import recipe_application.application.dto.forms.ingredientForm.CreateIngredientForm;
import recipe_application.application.dto.forms.ingredientForm.UpdateIngredientForm;
import recipe_application.application.dto.views.IngredientView;

import javax.validation.Valid;


@Controller
@RequestMapping("/ingredient")
public class IngredientController {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }


    public String getAllIngredientByName(@RequestParam("name") String name, Model model) {
        model.addAttribute("ingredientList", ingredientService.findByIngredientNameContainsIgnoreCase(name));

        return "ingredient/ingredients";
    }

    public String getIngredientByName(@RequestParam("name") String name, Model model) {
        model.addAttribute("ingredient", ingredientService.findByIngredientNameIgnoreCase(name));

        return "ingredient/ingredient";
    }

    @GetMapping("/search")
    public String searchForIngredient(@RequestParam("search") String search, Model model)  {
        if(ingredientService.findByIngredientNameContainsIgnoreCase(search.toLowerCase()).size() > 0){
            return getAllIngredientByName(search, model);
        }

        return getIngredientByName(search, model);
    }

    @GetMapping("/list")
    public String getIngredientList(Model model) {
        model.addAttribute("ingredientList", ingredientService.findAll());

        return "ingredient/ingredients";
    }

    @GetMapping("/get/{id}")
    public String getIngredientById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("ingredient", ingredientService.findById(id));

        return "ingredient/ingredient";
    }

    @GetMapping("/add")
    public String addIngredient(Model model) {
        CreateIngredientForm createIngredientForm = new CreateIngredientForm();
        model.addAttribute("ingredient", createIngredientForm);

        return "ingredient/add-ingredient";
    }

    @PostMapping("/add")
    public String addIngredient(@Valid @ModelAttribute("ingredient") CreateIngredientForm createIngredientForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (ingredientService.findByIngredientNameIgnoreCase(createIngredientForm.getIngredientName()) != null) {
            String message = "Duplicate! " + createIngredientForm.getIngredientName() + " already exist.";
            FieldError fieldError = new FieldError("ingredient", "ingredientName", message);
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            return "ingredient/add-ingredient";
        }

        ingredientService.save(createIngredientForm);

        return "redirect:/ingredient/list";
    }

    @GetMapping("/update/{id}")
    public String updateIngredient(@PathVariable("id") Integer id, Model model) {
        IngredientView ingredientView = ingredientService.findById(id);
        UpdateIngredientForm updateIngredientForm = new UpdateIngredientForm();

        updateIngredientForm.setId(id);
        updateIngredientForm.setIngredientName(ingredientView.getIngredientName());

        model.addAttribute("ingredient", updateIngredientForm);

        return "ingredient/update-ingredient";
    }

    @PostMapping("/update")
    public String updateIngredient(@ModelAttribute("ingredient") @Valid UpdateIngredientForm updateIngredientForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "ingredient/update-ingredient";
        }

        ingredientService.update(updateIngredientForm);

        return "redirect:/ingredient/list";
    }

    @GetMapping("/delete/request/{id}")
    public String deleteById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("ingredient", ingredientService.findById(id));
        return "ingredient/delete-request";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Integer id, RedirectAttributes ra) {
        ingredientService.deleteById(id);
        return "redirect:/ingredient/list";
    }

}
