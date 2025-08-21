package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.entity.PokemonAbility;
import com.pokeanalytics.pokedexdataservice.mapper.PokemonAbilityMapper;
import com.pokeanalytics.pokedexdataservice.service.PokemonAbilityService;
import org.springframework.stereotype.Service;

@Service
public class PokemonAbilityServiceImpl extends ServiceImpl<PokemonAbilityMapper, PokemonAbility> implements PokemonAbilityService {
}
