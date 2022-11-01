package recipe_application.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import recipe_application.application.data.service.RecipeInstructionService;
import recipe_application.application.dto.forms.recipeInstructionForm.CreateRecipeInstructionForm;
import recipe_application.application.dto.forms.recipeInstructionForm.UpdateRecipeInstructionForm;
import recipe_application.application.dto.views.RecipeInstructionView;

import javax.validation.Valid;


@Controller
@RequestMapping("/recipe-instruction")
public class RecipeInstructionController {

    private final RecipeInstructionService recipeInstructionService;

    @Autowired
    public RecipeInstructionController(RecipeInstructionService recipeInstructionService) {
        this.recipeInstructionService = recipeInstructionService;
    }

    @GetMapping("/list")
    public String getRecipeInstructionList(Model model) {
        model.addAttribute("recipeInstructionList", recipeInstructionService.findAll());

        return "recipe-instruction/recipe-instructions";
    }

    @GetMapping("/get/{id}")
    public String getRecipeInstructionById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipeInstruction", recipeInstructionService.findById(id));

        return "recipe-instruction/recipe-instruction";
    }

    @GetMapping("/add")
    public String addRecipeInstruction(Model model) {
        CreateRecipeInstructionForm createRecipeInstructionForm = new CreateRecipeInstructionForm();
        model.addAttribute("recipeInstruction", createRecipeInstructionForm);

        return "recipe-instruction/add-recipe-instruction";
    }

    @PostMapping("/add")
    public String addRecipeInstruction(@Valid @ModelAttribute("recipeInstruction") CreateRecipeInstructionForm createRecipeInstructionForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-instruction/add-recipe-instruction";
        }

        recipeInstructionService.save(createRecipeInstructionForm);

        return "redirect:/recipe-instruction/list";
    }

    @GetMapping("/update/{id}")
    public String updateRecipeInstruction(@PathVariable("id") Integer id, Model model) {
        RecipeInstructionView recipeInstructionView = recipeInstructionService.findById(id);
        UpdateRecipeInstructionForm updateRecipeInstructionForm = new UpdateRecipeInstructionForm();

        updateRecipeInstructionForm.setId(id);
        updateRecipeInstructionForm.setTitle(recipeInstructionView.getTitle());
        updateRecipeInstructionForm.setInstruction(recipeInstructionView.getInstruction());

        model.addAttribute("recipeInstruction", updateRecipeInstructionForm);

        return "recipe-instruction/update-recipe-instruction";
    }

    @PostMapping("/update")
    public String updateRecipeInstruction(@ModelAttribute("recipeInstruction") @Valid UpdateRecipeInstructionForm updateRecipeInstructionForm, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "recipe-instruction/update-recipe-instruction";
        }

        recipeInstructionService.update(updateRecipeInstructionForm);

        return "redirect:/recipe-instruction/list";
    }

    @GetMapping("/delete/request/{id}")
    public String deleteById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipeInstruction", recipeInstructionService.findById(id));
        return "recipe-instruction/delete-request";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Integer id, RedirectAttributes ra) {
        recipeInstructionService.deleteById(id);
        return "redirect:/recipe-instruction/list";
    }

}



























