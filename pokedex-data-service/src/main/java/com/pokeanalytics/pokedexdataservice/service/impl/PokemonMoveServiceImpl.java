// service/impl/PokemonMoveServiceImpl.java
package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.entity.PokemonMove;
import com.pokeanalytics.pokedexdataservice.mapper.PokemonMoveMapper;
import com.pokeanalytics.pokedexdataservice.service.PokemonMoveService;
import org.springframework.stereotype.Service;

@Service
public class PokemonMoveServiceImpl extends ServiceImpl<PokemonMoveMapper, PokemonMove> implements PokemonMoveService {
}