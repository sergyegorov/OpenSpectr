/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.db;

import java.awt.Color;
import org.tl.ops.Common;

/**
 *
 * @author root
 */
public class ElementTable {
    public static int FindIndex(String name)
    {
        for (int i = 0; i < Elements.length; i++)
            if (Elements[i].Name.toLowerCase().trim().equals(name.toLowerCase().trim()))
                return i;
        return -1;
    }

    static public Color GetColorForType(ElementTypes type)
    {
        switch (type)
        {
            case Galogeni: return new Color(255, 251, 160);
            case InertnieGazi: return new Color(198, 252, 252);
            case KantMolek: return Color.yellow;
            case LiogkieMetali: return new Color(207, 203, 205);
            case Nemetali: return new Color(152, 255, 172);
            case Perehodnie: return new Color(255, 189, 186);
            case Polumetali: return new Color(206, 203, 150);
            case ShelochnieMetali: return new Color(255, 90, 87);
            case ShelochnozemelnijMetali: return new Color(255, 220, 162);
            case Aktinoidi: return new Color(237, 154, 208);
            case Lantanoidi: return new Color(255, 193, 255);
            default: return Color.white;
        }
    }

    static public String GetNameOfType(ElementTypes type)
    {
        switch (type)
        {
            case Galogeni: return Common.Mls.get("ElTable", "Галогены");
            case InertnieGazi: return Common.Mls.get("ElTable", "Инертные газы");
            case KantMolek: return Common.Mls.get("ElTable", "Кант молекуларной полосы");
            case LiogkieMetali: return Common.Mls.get("ElTable", "Лёгкие металлы");
            case Nemetali: return Common.Mls.get("ElTable", "Неметаллы");
            case Perehodnie: return Common.Mls.get("ElTable", "Переходные элементы");
            case Polumetali: return Common.Mls.get("ElTable", "Полуметаллы");
            case ShelochnieMetali: return Common.Mls.get("ElTable", "Щелочные металлы");
            case ShelochnozemelnijMetali: return Common.Mls.get("ElTable", "Щелочноземельные металлы");
            case Aktinoidi: return Common.Mls.get("ElTable", "Актиноиды");
            case Lantanoidi: return Common.Mls.get("ElTable", "Лантаноиды");
            default: return Common.Mls.get("ElTable", "Неизвестные");
        }
    }

    static public Element[] Elements = 
    {
        new Element(1,"H","Водород",ElementTypes.Nemetali,1,1),
        new Element(2,"He","Гелий",ElementTypes.InertnieGazi,18,1),

        new Element(3,"Li","Литий",ElementTypes.ShelochnieMetali,1,2),
        new Element(4,"Be","Берилий",ElementTypes.ShelochnozemelnijMetali,2,2),
        new Element(5,"B","Бор",ElementTypes.Polumetali,13,2),
        new Element(6,"C","Углерод",ElementTypes.Nemetali,14,2),
        new Element(7,"N","Азот",ElementTypes.Nemetali,15,2),
        new Element(8,"O","Кислород",ElementTypes.Nemetali,16,2),
        new Element(9,"F","Фтор",ElementTypes.Galogeni,17,2),
        new Element(10,"Ne","Неон",ElementTypes.InertnieGazi,18,2),

        new Element(11,"Na","Натрий",ElementTypes.ShelochnieMetali,1,3),
        new Element(12,"Mg","Магний",ElementTypes.ShelochnozemelnijMetali,2,3),
        new Element(13,"Al","Алюминий",ElementTypes.LiogkieMetali,13,3),
        new Element(14,"Si","Кремний",ElementTypes.Polumetali,14,3),
        new Element(15,"P","Фосфор",ElementTypes.Nemetali,15,3),
        new Element(16,"S","Сера",ElementTypes.Nemetali,16,3),
        new Element(17,"Cl","Хлор",ElementTypes.Galogeni,17,3),
        new Element(18,"Ar","Аргон",ElementTypes.InertnieGazi,18,3),

        new Element(19,"K","Калий",ElementTypes.ShelochnieMetali,1,4),
        new Element(20,"Ca","Кальций",ElementTypes.ShelochnozemelnijMetali,2,4),
        new Element(21,"Sc","Скандий",ElementTypes.Perehodnie,3,4),
        new Element(22,"Ti","Титан",ElementTypes.Perehodnie,4,4),
        new Element(23,"V","Ванадий",ElementTypes.Perehodnie,5,4),
        new Element(24,"Cr","Хром",ElementTypes.Perehodnie,6,4),
        new Element(25,"Mn","Марганец",ElementTypes.Perehodnie,7,4),
        new Element(26,"Fe","Железо",ElementTypes.Perehodnie,8,4),
        new Element(27,"Co","Кобальт",ElementTypes.Perehodnie,9,4),
        new Element(28,"Ni","Никель",ElementTypes.Perehodnie,10,4),
        new Element(29,"Cu","Медь",ElementTypes.Perehodnie,11,4),
        new Element(30,"Zn","Цинк",ElementTypes.Perehodnie,12,4),
        new Element(31,"Ga","Галий",ElementTypes.LiogkieMetali,13,4),
        new Element(32,"Ge","Германий",ElementTypes.Polumetali,14,4),
        new Element(33,"As","Мышьяк",ElementTypes.Polumetali,15,4),
        new Element(34,"Se","Селен",ElementTypes.Nemetali,16,4),
        new Element(35,"Br","Бром",ElementTypes.Galogeni,17,4),
        new Element(36,"Kr","Криптон",ElementTypes.InertnieGazi,18,4),

        new Element(37,"Rb","Рубидий",ElementTypes.ShelochnieMetali,1,5),
        new Element(38,"Sr","Стронций",ElementTypes.ShelochnozemelnijMetali,2,5),
        new Element(39,"Y","Иттрий",ElementTypes.Perehodnie,3,5),
        new Element(40,"Zr","Цирконий",ElementTypes.Perehodnie,4,5),
        new Element(41,"Nb","Ниобий",ElementTypes.Perehodnie,5,5),
        new Element(42,"Mo","Молибден",ElementTypes.Perehodnie,6,5),
        new Element(43,"Tc","Технеций",ElementTypes.Perehodnie,7,5,true),
        new Element(44,"Ru","Рутений",ElementTypes.Perehodnie,8,5),
        new Element(45,"Rh","Родий",ElementTypes.Perehodnie,9,5),
        new Element(46,"Pd","Палладий",ElementTypes.Perehodnie,10,5),
        new Element(47,"Ag","Серебро",ElementTypes.Perehodnie,11,5),
        new Element(48,"Cd","Кадмий",ElementTypes.Perehodnie,12,5),
        new Element(49,"In","Индий",ElementTypes.LiogkieMetali,13,5),
        new Element(50,"Sn","Олово",ElementTypes.LiogkieMetali,14,5),
        new Element(51,"Sb","Сурьма",ElementTypes.Polumetali,15,5),
        new Element(52,"Te","Теллур",ElementTypes.Polumetali,16,5),
        new Element(53,"I","Иод",ElementTypes.Galogeni,17,5),
        new Element(54,"Xe","Ксенон",ElementTypes.InertnieGazi,18,5),

        new Element(55,"Cs","",ElementTypes.ShelochnieMetali,1,6),
        new Element(56,"Ba","",ElementTypes.ShelochnozemelnijMetali,2,6),
        // lantonoids
        new Element(252,"*","Лантаноиды",ElementTypes.Lantanoidi,3,6),

        new Element(57,"La","",ElementTypes.Lantanoidi,1,9),
        new Element(58,"Ce","",ElementTypes.Lantanoidi,2,9),
        new Element(59,"Pr","",ElementTypes.Lantanoidi,3,9),
        new Element(60,"Nd","",ElementTypes.Lantanoidi,4,9),
        new Element(61,"Pm","",ElementTypes.Lantanoidi,5,9),
        new Element(62,"Sm","",ElementTypes.Lantanoidi,6,9),
        new Element(63,"Eu","",ElementTypes.Lantanoidi,7,9),
        new Element(64,"Gd","",ElementTypes.Lantanoidi,8,9),
        new Element(65,"Tb","",ElementTypes.Lantanoidi,9,9),
        new Element(66,"Dy","",ElementTypes.Lantanoidi,10,9),
        new Element(67,"Ho","",ElementTypes.Lantanoidi,11,9),
        new Element(68,"Er","",ElementTypes.Lantanoidi,12,9),
        new Element(69,"Tm","",ElementTypes.Lantanoidi,13,9),
        new Element(70,"Yb","",ElementTypes.Lantanoidi,14,9),
        new Element(71,"Lu","",ElementTypes.Lantanoidi,15,9),
        new Element(72,"Hf","",ElementTypes.Perehodnie,4,6),
        new Element(73,"Ta","",ElementTypes.Perehodnie,5,6),
        new Element(74,"W","",ElementTypes.Perehodnie,6,6),
        new Element(75,"Re","",ElementTypes.Perehodnie,7,6),
        new Element(76,"Os","",ElementTypes.Perehodnie,8,6),
        new Element(77,"Ir","",ElementTypes.Perehodnie,9,6),
        new Element(78,"Pt","",ElementTypes.Perehodnie,10,6),
        new Element(79,"Au","",ElementTypes.Perehodnie,11,6),
        new Element(80,"Hg","",ElementTypes.Perehodnie,12,6),
        new Element(81,"Tl","",ElementTypes.LiogkieMetali,13,6),
        new Element(82,"Pb","",ElementTypes.LiogkieMetali,14,6),
        new Element(83,"Bi","",ElementTypes.LiogkieMetali,15,6),
        new Element(84,"Po","",ElementTypes.Polumetali,16,6),
        new Element(85,"At","",ElementTypes.Galogeni,17,6,true),
        new Element(86,"Rn","",ElementTypes.InertnieGazi,18,6),

        new Element(87,"Fr","",ElementTypes.ShelochnieMetali,1,7),
        new Element(88,"Ra","",ElementTypes.ShelochnozemelnijMetali,2,7),
        // actinoids
        new Element(253,"**","",ElementTypes.Aktinoidi,3,7),

        new Element(89,"Ac","",ElementTypes.Aktinoidi,1,10),
        new Element(90,"Th","",ElementTypes.Aktinoidi,2,10),
        new Element(91,"Pa","",ElementTypes.Aktinoidi,3,10),
        new Element(92,"U","",ElementTypes.Aktinoidi,4,10,true),
        new Element(93,"Np","",ElementTypes.Aktinoidi,5,10,true),
        new Element(94,"Pu","",ElementTypes.Aktinoidi,6,10,true),
        new Element(95,"Am","",ElementTypes.Aktinoidi,7,10,true),
        new Element(96,"Cm","",ElementTypes.Aktinoidi,8,10,true),
        new Element(97,"Bk","",ElementTypes.Aktinoidi,9,10,true),
        new Element(98,"Cf","",ElementTypes.Aktinoidi,10,10,true),
        new Element(99,"Es","",ElementTypes.Aktinoidi,11,10,true),
        new Element(100,"Fm","",ElementTypes.Aktinoidi,12,10,true),
        new Element(101,"Md","",ElementTypes.Aktinoidi,13,10,true),
        new Element(102,"No","",ElementTypes.Aktinoidi,14,10,true),
        new Element(103,"Lr","",ElementTypes.Aktinoidi,15,10,true),
        new Element(104,"Rf","",ElementTypes.Perehodnie,4,7,true),
        new Element(105,"Db","",ElementTypes.Perehodnie,5,7,true),
        new Element(106,"Sg","",ElementTypes.Perehodnie,6,7,true),
        new Element(107,"Bh","",ElementTypes.Perehodnie,7,7,true),
        new Element(108,"Hs","",ElementTypes.Perehodnie,8,7,true),
        new Element(109,"Mt","",ElementTypes.Perehodnie,9,7,true),
        new Element(110,"Ds","",ElementTypes.Perehodnie,10,7,true),
        new Element(111,"Rg","",ElementTypes.Perehodnie,11,7,true),
        new Element(112,"Cp","",ElementTypes.Perehodnie,12,7,true),
        //new Element(113,"Uut","",ElementTypes.LiogkieMetali,13,7,true),
        new Element(254,"k","Кант молекуларной полосы",ElementTypes.KantMolek,18,7,false)
    };
}
