-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Dec 04, 2016 at 09:05 PM
-- Server version: 5.5.49-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pfe`
--
CREATE DATABASE IF NOT EXISTS `pfe` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `pfe`;

-- --------------------------------------------------------

--
-- Table structure for table `data_files`
--

DROP TABLE IF EXISTS `data_files`;
CREATE TABLE IF NOT EXISTS `data_files` (
  `time_ms` smallint(4) NOT NULL,
  `type` tinyint(2) NOT NULL,
  `minimum` double NOT NULL,
  `file_name` varchar(15) COLLATE utf8_bin NOT NULL,
  `isGood` tinyint(1) NOT NULL,
  PRIMARY KEY (`time_ms`,`type`,`minimum`,`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `data_files`
--

INSERT INTO `data_files` (`time_ms`, `type`, `minimum`, `file_name`, `isGood`) VALUES
(500, 1, 0, 'file1', 0);

-- --------------------------------------------------------

--
-- Table structure for table `probabilities`
--

DROP TABLE IF EXISTS `probabilities`;
CREATE TABLE IF NOT EXISTS `probabilities` (
  `time_ms` smallint(4) NOT NULL,
  `type` tinyint(2) NOT NULL,
  `minimum` double NOT NULL,
  `proba_yes` double NOT NULL,
  `proba_no` double NOT NULL,
  PRIMARY KEY (`time_ms`,`type`,`minimum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `probabilities`
--

INSERT INTO `probabilities` (`time_ms`, `type`, `minimum`, `proba_yes`, `proba_no`) VALUES
(500, 1, 0, 0.3, 0.7);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
