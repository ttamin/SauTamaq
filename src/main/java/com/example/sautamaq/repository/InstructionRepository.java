package com.example.sautamaq.repository;

import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
    Optional<Instruction> findById(Long instructionId);

}
