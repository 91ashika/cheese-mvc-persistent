package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {
    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menu");

        return "menu/index";
    }
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                       Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:/menu/view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id){
        Menu menu = menuDao.findOne(id);
        model.addAttribute("menu",menu);
        model.addAttribute("title",menu.getName());
        return "menu/view";
    }
    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id){
        Menu menu = menuDao.findOne(id);
        AddMenuItemForm form = new AddMenuItemForm(menu,cheeseDao.findAll());
        model.addAttribute("form",form);
        model.addAttribute("title","Add item to menu:"+menu.getName());

        return "menu/add-item";

    }
    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String addItem(Model model,@ModelAttribute @Valid AddMenuItemForm form, Errors errors,@PathVariable int id){
        if(errors.hasErrors()){
            model.addAttribute("form",form);
            return "menu/add-item";
        }
        Cheese cheese = cheeseDao.findOne(form.getCheeseId());
        Menu menu = menuDao.findOne(form.getMenuId());
        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:/menu/view/" + menu.getId();
    }
}
