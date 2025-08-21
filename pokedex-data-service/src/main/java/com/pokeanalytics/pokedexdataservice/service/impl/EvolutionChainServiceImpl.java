// service/impl/EvolutionChainServiceImpl.java
package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.entity.EvolutionChain;
import com.pokeanalytics.pokedexdataservice.mapper.EvolutionChainMapper;
import com.pokeanalytics.pokedexdataservice.service.EvolutionChainService;
import org.springframework.stereotype.Service;

@Service
public class EvolutionChainServiceImpl extends ServiceImpl<EvolutionChainMapper, EvolutionChain> implements EvolutionChainService {
}